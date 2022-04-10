(ns build
  "The build script for the Polylith project.

  Primary targets:
  * create-artifacts
    - creates uberjar and homebrew scripts
  * deploy
    - creates & deploys project JARs
    - perform a dry run with :installer :local

  Additional targets:
  * jar :project PROJECT
    - creates a library JAR for the given project
  * scripts :project PROJECT
    - creates the homebrew scripts for the given project
  * uberjar :project PROJECT
    - creates an uberjar for the given project

  For help, run:

  clojure -A:deps -T:build help/doc"
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.tools.build.api :as b]
            [clojure.tools.deps.alpha :as t]
            [clojure.tools.deps.alpha.util.dir :refer [with-dir]]
            [org.corfield.build :as bb]
            [polylith.clj.core.version.interface :as version]))

(defn- get-project-aliases []
  (let [edn-fn (juxt :root-edn :project-edn)]
    (-> (t/find-edn-maps)
        (edn-fn)
        (t/merge-edns)
        :aliases)))

(defn- exec->out
  "Given options for b/process, run the command with stdout
  and stderr captured, returning stdout if successful.

  If the command fails, display stdout, stderr, and then
  exit with a non-zero status."
  [opts]
  (let [{:keys [exit out err]}
        (b/process (assoc opts :out :capture :err :capture))]
    (when-not (zero? exit)
      (println "Failed to execute:" (str/join " " (:command-args opts)))
      (when (seq out)
        (println "Output:")
        (println out))
      (when (seq err)
        (println "Errors:")
        (println err))
      (System/exit 1))
    out))

(defn- ensure-project-root
  "Given a task name and a project name, ensure the project
  exists and seems valid, and return the absolute path to it."
  [task project]
  (let [project-root (str (System/getProperty "user.dir") "/projects/" project)]
    (when-not (and project
                   (.exists (io/file project-root))
                   (.exists (io/file (str project-root "/deps.edn"))))
      (throw (ex-info (str task " task requires a valid :project option") {:project project})))
    project-root))

(defn- changed-projects
  "Run the poly tool (from source) to get a list of changed projects."
  []
  (-> (b/java-command
       {:basis     (binding [b/*project-root* (ensure-project-root "changed" "poly")]
                     (b/create-basis))
        :main      'clojure.main
        :main-args ["-m" "polylith.clj.core.poly-cli.core"
                    "ws" "get:changes:changed-or-affected-projects" "skip:dev"
                    "since:previous-release"
                    "color-mode:none"]})
      (exec->out)
      (edn/read-string)))

(defn scripts
  "Produce the artifacts scripts for the specified project.

  Returns: the input opts with :artifact-path and :artifact-name
  computed as the path and just the filename of the expected
  (uber) JAR file."
  [{:keys [project] :as opts}]
  (ensure-project-root "scripts" project)
  (let [project     (name project)
        project-dir (str "artifacts/" project)]
    (b/copy-dir {:src-dirs   [(.getFile (io/resource "brew"))]
                 :target-dir project-dir
                 :replace    {"{{PROJECT}}"  project
                              "{{PROJECT_}}" (str/replace project #"-" "_")
                              "{{VERSION}}"  version/name}})
    (b/copy-file {:src    (str project-dir "/exec")
                  :target (str project-dir "/" project)})
    (b/delete {:path (str project-dir "/exec")})
    (exec->out {:command-args ["chmod" "+x" "install.sh" project]
                :dir          project-dir})
    (let [artifact-name (str project "-" version/name ".jar")]
      (assoc opts
             :artifact-path (str project-dir "/" artifact-name)
             :artifact-name artifact-name))))

(defn jar
  "Builds a library jar for the specified project.

  Options:
  * :project - required, the name of the project to build,
  * :jar-file - optional, the path of the JAR file to build,
    relative to the project folder; can also be specified in
    the :jar alias in the project's deps.edn file; will
    default to target/PROJECT-thin.jar if not specified.

  Returns:
  * the input opts with :class-dir, :jar-file, :lib, :pom-file,
    and :version computed.

  Because we build JARs from Polylith projects, all the source
  code we want in the JAR comes from :local/root dependencies of
  the project and the actual dependencies are transitive to those
  :local/root dependencies, so we use the :transitive option of
  build-clj."
  [{:keys [project jar-file] :as opts}]
  (let [project-root (ensure-project-root "jar" project)
        aliases      (with-dir (io/file project-root) (get-project-aliases))]
    (binding [b/*project-root* project-root]
      (let [class-dir "target/classes"
            jar-file  (or jar-file
                          (-> aliases :jar :jar-file)
                          (str "target/" project "-thin.jar"))
            lib       (symbol "polylith" (str "clj-" project))
            opts      (merge opts
                             {:class-dir  class-dir
                              :jar-file   jar-file
                              :lib        lib
                              :src-pom    "partial_pom.xml"
                              :transitive true
                              :version    version/name})]
        (b/delete {:path class-dir})
        (bb/jar opts)
        ;; we want the pom.xml file in the project folder for deployment:
        (b/copy-file {:src    (b/pom-path {:class-dir class-dir
                                           :lib       lib})
                      :target "pom.xml"})
        (b/delete {:path class-dir})
        (println "Jar is built.")
        (-> opts
            (assoc :pom-file (str project-root "/pom.xml"))
            ;; account for project root relative paths:
            (update :jar-file (comp #(.getCanonicalPath %) b/resolve-path)))))))

(defn uberjar
  "Builds an uberjar for the specified project.

  Options:
  * :project - required, the name of the project to build,
  * :uber-file - optional, the path of the JAR file to build,
    relative to the project folder; can also be specified in
    the :uberjar alias in the project's deps.edn file; will
    default to target/PROJECT.jar if not specified.

  Returns:
  * the input opts with :class-dir, :compile-opts, :main, and :uber-file
    computed.

  The project's deps.edn file must contain an :uberjar alias
  which must contain at least :main, specifying the main ns
  (to compile and to invoke)."
  [{:keys [project uber-file] :as opts}]
  (let [project-root (ensure-project-root "uberjar" project)
        aliases      (with-dir (io/file project-root) (get-project-aliases))
        main         (-> aliases :uberjar :main)]
    (when-not main
      (throw (ex-info (str "the " project " project's deps.edn file does not specify the :main namespace in its :uberjar alias")
                      {:aliases aliases})))
    (binding [b/*project-root* project-root]
      (let [class-dir "target/classes"
            uber-file (or uber-file
                          (-> aliases :uberjar :uber-file)
                          (str "target/" project ".jar"))
            opts      (merge opts
                             {:class-dir    class-dir
                              :compile-opts {:direct-linking true}
                              :main         main
                              :uber-file    uber-file})]
        (b/delete {:path class-dir})
        (bb/uber opts)
        (b/delete {:path class-dir})
        (println "Uberjar is built.")
        opts))))

(defn deploy
  "Create and deploy library JAR files for the Polylith project.

  Currently, creates and deploys 'api' and 'poly'.

  You can do a dry run by passing :installer :local which will
  deploy the JARs into your local Maven cache instead of to Clojars."
  [opts]
  (let [changed (changed-projects)
        projects-to-process (filter #{"api" "poly"} changed)]
    (when-not (seq projects-to-process)
      (throw (ex-info "Cannot deploy projects. No projects have changed."
                      {:changed changed})))
    (doseq [project projects-to-process]
      (let [project-opts (assoc opts :project project)]
        (println (str "Starting deployment for " project " project."))
        (-> project-opts (jar) (bb/deploy))
        (println (str "Deployment completed for " project " project."))))))

(defn create-artifacts
  "Create the artifacts for the Polylith project.

  Currently, this only creates artifacts for 'poly'.

  It creates an artifacts directory, containing an uberjar,
  a '.tar.gz' of the uberjar and install.sh and a project
  executable script, and a '.sha1' file for that '.tar.gz'."
  [opts]
  (let [changed (changed-projects)
        projects-to-process (filter #{"poly"} changed)]
    (when-not (seq projects-to-process)
      (throw (ex-info "Cannot create artifacts. No projects have changed."
                      {:changed changed})))
    (b/delete {:path "artifacts"})
    (doseq [project projects-to-process]
      (println (str "Creating artifacts for: " project))
      (let [project-opts        (assoc opts :project project)
            {:keys [uber-file]} (uberjar project-opts)
            {:keys [artifact-path artifact-name]} (scripts project-opts)]
        (b/copy-file {:src    (str "projects/" project "/" uber-file)
                      :target artifact-path})
        (b/copy-file {:src    artifact-path
                      :target (str "artifacts/" artifact-name)})
        (let [tar-file (str/replace artifact-name #"\.jar$" ".tar.gz")
              _        (exec->out {:command-args ["tar" "cvfz" tar-file project]
                                   :dir "artifacts"})
              sha-sum  (-> (exec->out {:command-args ["shasum" "-a" "256" tar-file]
                                       :dir "artifacts"})
                           (str/split #" ")
                           (first))]
          (println (str "Shasum for " project ": " sha-sum))
          (spit (str "artifacts/" tar-file ".sha256") (str sha-sum " " tar-file)))
        (b/delete {:path (str "artifacts/" project)})))))
