(ns polylith.clj.core.build.core
  "The build script for the Polylith project.

  To build an uberjar for a project, run:

  clojure -T:build uberjar :project PROJECT

  For help, run:

  clojure -A:deps -T:build help/doc"
  (:require [clojure.string :as str]
            [clojure.tools.build.api :as b]
            [clojure.tools.deps.alpha :as t]
            [clojure.tools.deps.alpha.util.dir :refer [with-dir]]
            ;[polylith.clj.core.api.interface :as api]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.version.interface :as version]
            [org.corfield.log4j2-conflict-handler
             :refer [log4j2-conflict-handler]]))

(defn- get-project-aliases []
  (let [edn-fn (juxt :root-edn :project-edn)]
    (-> (t/find-edn-maps)
        (edn-fn)
        (t/merge-edns)
        :aliases)))

(defn scripts
  "Produce the artifacts scripts for the specified project."
  [{:keys [project]}]
  (b/copy-dir {:src-dirs ["bases/build/resources/build"] :target-dir (str "artifacts/" project)
               :replace  {"{{PROJECT}}"  (name project)
                          "{{PROJECT_}}" (str/replace (name project) #"-" "_")
                          "{{VERSION}}"  version/name}})
  (b/copy-file {:src    (str "artifacts/" project "/exec")
                :target (str "artifacts/" project "/" project)})
  (b/delete {:path (str "artifacts/" project "/exec")}))

(defn jar
  "Builds a library jar for the specified project.

  Options:
  * :project - required, the name of the project to build,
  * :jar-file - optional, the path of the JAR file to build,
    relative to the project folder; can also be specified in
    the :jar alias in the project's deps.edn file; will
    default to target/PROJECT-thin.jar if not specified.

  Because we build JARs from Polylith projects, all the source
  code we want in the JAR comes from :local/root dependencies of
  the project and the actual dependencies are transitive to those
  :local/root dependencies, so we do some surgery on the basis in
  order to 'lift' the bricks' top-level dependencies and remove
  all the non-versioned dependencies -- so that the pom.xml is
  generated correctly. We also copy 'through' those :local/root
  dependencies to get src/resources etc into the JAR."
  [{:keys [project jar-file] :as opts}]
  (let [project-root (str (file/current-dir) "/projects/" project)
        _
        (when-not (and project
                       (file/exists project-root)
                       (file/exists (str project-root "/deps.edn")))
          (throw (ex-info "jar task requires a valid :project option" {:opts opts})))
        aliases (with-dir (file/file project-root) (get-project-aliases))]
    (binding [b/*project-root* project-root]
      (let [basis     (b/create-basis)
            ;; compute the transitive dependencies that come directly from
            ;; poly/* bricks within the project:
            poly-deps (into {}
                            (comp (filter (fn [[_ {:keys [mvn/version dependents]}]]
                                       (and version
                                            (some (fn [lib] (= "poly" (namespace lib)))
                                                  dependents))))
                                  (map (fn [[lib {:keys [mvn/version]}]]
                                         [lib {:mvn/version version}])))
                            (:libs basis))
            ;; recompute the basis as if those were top-level dependencies:
            basis     (b/create-basis {:extra {:deps poly-deps}})
            ;; and then remove :local/root dependencies from :libs:
            basis     (update basis
                              :libs #(into {}
                                           (filter (fn [[_ {:keys [mvn/version]}]] version))
                                           %))
            class-dir "target/classes"
            jar-file  (or jar-file
                          (-> aliases :jar :jar-file)
                          (str "target/" project "-thin.jar"))
            lib       (symbol "polylith" (str "clj-" project))]
        (b/delete {:path class-dir})
        (println "Writing pom.xml ...")
        (b/write-pom {:basis     basis
                      :class-dir class-dir
                      :lib       lib
                      :resource-dirs []
                      :src-dirs  []
                      :src-pom   "partial_pom.xml"
                      :version   version/name})
        ;; and we want that pom.xml file in the target folder for deployment:
        (b/copy-file {:src    (b/pom-path {:class-dir class-dir
                                           :lib       lib})
                      :target "pom.xml"})
        (println "Copying files ...")
        ;; copy all the source files on the classpath (i.e., from local bricks):
        (b/copy-dir {:src-dirs   (filter (comp file/directory? file/file)
                                         (:classpath-roots basis))
                     :target-dir class-dir})
        (println "Building jar:" jar-file "...")
        (b/jar {:class-dir class-dir
                :jar-file jar-file})
        (b/delete {:path class-dir})))))

(defn uberjar
  "Builds an uberjar for the specified project.

  Options:
  * :project - required, the name of the project to build,
  * :uber-file - optional, the path of the JAR file to build,
    relative to the project folder; can also be specified in
    the :uberjar alias in the project's deps.edn file; will
    default to target/PROJECT.jar if not specified.

  The project's deps.edn file must contain an :uberjar alias
  which must contain at least :main, specifying the main ns
  (to compile and to invoke)."
  [{:keys [project uber-file] :as opts}]
  (let [project-root (str (file/current-dir) "/projects/" project)
        _
        (when-not (and project
                       (file/exists project-root)
                       (file/exists (str project-root "/deps.edn")))
          (throw (ex-info "uberjar task requires a valid :project option" {:opts opts})))
        aliases (with-dir (file/file project-root) (get-project-aliases))
        main    (-> aliases :uberjar :main)]
    (when-not main
      (throw (ex-info (str "the " project " project's deps.edn file does not specify the :main namespace in its :uberjar alias")
                      {:aliases aliases})))
    (binding [b/*project-root* project-root]
      (let [basis     (b/create-basis)
            class-dir "target/classes"
            uber-file (or uber-file
                          (-> aliases :uberjar :uber-file)
                          (str "target/" project ".jar"))]
        (b/delete {:path class-dir})
        (println "Compiling" main "...")
        (b/compile-clj {:basis        basis
                        :class-dir    class-dir
                        :compile-opts {:direct-linking true}
                        :ns-compile   [main]
                        :src-dirs     []})
        (println "Building uber jar:" uber-file "...")
        (b/uber {:basis     basis
                 :class-dir class-dir
                 :conflict-handlers
                 log4j2-conflict-handler
                 :main      main
                 :uber-file uber-file})
        (b/delete {:path class-dir})))))

(defn deploy [opts]
  (println "deploy!"))

(defn create-artifacts [opts]
  (println "create-artifacts!"))
