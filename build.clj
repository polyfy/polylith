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
            [clojure.set :as set]
            [clojure.string :as str]
            [clojure.tools.build.api :as b]
            [clojure.tools.deps :as t]
            [clojure.tools.deps.util.dir :refer [with-dir]]
            [deps-deploy.deps-deploy :as d]
            [polylith.clj.core.git.interface :as git]
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

(defn- latest-committed-sha
  "Get the latest committed SHA from current branch."
  []
  (let [branch (git/current-branch)]
    (git/latest-polylith-sha branch)))

(defn- changed-projects
  "Run the poly tool (from source) to get a list of changed projects."
  []
  (-> (b/java-command
       {:basis (binding [b/*project-root* (ensure-project-root "changed" "poly")]
                 (b/create-basis))
        :main 'clojure.main
        :main-args ["-m" "polylith.clj.core.poly-cli.core"
                    "ws" "get:changes:changed-or-affected-projects" "skip:dev:polyx"
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
    (b/copy-dir {:src-dirs [(.getFile (io/resource "brew"))]
                 :target-dir project-dir
                 :replace {"{{PROJECT}}"  project
                           "{{PROJECT_}}" (str/replace project #"-" "_")
                           "{{VERSION}}"  version/name}})
    (b/copy-file {:src (str project-dir "/exec")
                  :target (str project-dir "/" project)})
    (b/delete {:path (str project-dir "/exec")})
    (exec->out {:command-args ["chmod" "+x" "install.sh" project]
                :dir project-dir})
    (let [artifact-name (str project "-" version/name ".jar")]
      (assoc opts
             :artifact-path (str project-dir "/" artifact-name)
             :artifact-name artifact-name))))

(defn- lifted-basis
  "This creates a basis where source deps have their primary
   external dependencies lifted to the top-level, such as is
   needed by Polylith and possibly other monorepo setups."
  []
  (let [default-libs (:libs (b/create-basis))
        source-dep? #(not (:mvn/version (get default-libs %)))
        lifted-deps
        (reduce-kv (fn [deps lib {:keys [dependents] :as coords}]
                     (if (and (contains? coords :mvn/version) (some source-dep? dependents))
                       (assoc deps lib (select-keys coords [:mvn/version :exclusions]))
                       deps))
                   {}
                   default-libs)]
    (-> (b/create-basis {:extra {:deps lifted-deps}})
        (update :libs #(into {} (filter (comp :mvn/version val)) %)))))

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
   :local/root dependencies, so we create a 'lifted' basis.

   Example: clojure -T:build jar :project poly"
  [{:keys [project jar-file] :as opts}]
  (let [project-root (ensure-project-root "jar" project)
        aliases (with-dir (io/file project-root) (get-project-aliases))]
    (b/with-project-root project-root
      (let [basis (lifted-basis)
            class-dir "target/classes"
            jar-file (or jar-file
                         (-> aliases :jar :jar-file)
                         (str "target/" project "-thin.jar"))
            lib (symbol "polylith" (str "clj-" project))
            current-dir (System/getProperty "user.dir")
            current-rel #(str/replace % (str current-dir "/") "")
            directory? #(let [f (java.io.File. %)]
                          (and (.exists f) (.isDirectory f)))
            src+dirs (filter directory? (:classpath-roots basis))
            opts (merge opts
                        {:basis basis
                         :class-dir class-dir
                         :jar-file jar-file
                         :lib lib
                         :scm {:tag (if (= "SNAPSHOT" version/revision)
                                      (latest-committed-sha)
                                      (str "v" version/name))
                               :name "git"
                               :url "https://github.com/polyfy/polylith"}
                         :src-pom "partial_pom.xml"
                         :version version/name})]
        (b/delete {:path class-dir})
        (println "\nWriting pom.xml...")
        (b/write-pom opts)
        (println "Copying" (str (str/join ", " (map current-rel src+dirs)) "..."))
        (b/copy-dir {:src-dirs src+dirs
                     :target-dir class-dir})
        (println "Building jar" (str jar-file "..."))
        (b/jar opts)
        ;; we want the pom.xml file in the project folder for deployment:
        (b/copy-file {:src (b/pom-path {:class-dir class-dir
                                        :lib lib})
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
        aliases (with-dir (io/file project-root) (get-project-aliases))
        main (-> aliases :uberjar :main)]
    (when-not main
      (throw (ex-info (str "the " project " project's deps.edn file does not specify the :main namespace in its :uberjar alias")
                      {:aliases aliases})))
    (b/with-project-root project-root
      (let [class-dir "target/classes"
            uber-file (or uber-file
                          (-> aliases :uberjar :uber-file)
                          (str "target/" project ".jar"))
            opts (merge opts
                        {:basis (b/create-basis)
                         :class-dir class-dir
                         :compile-opts {:direct-linking true}
                         :main main
                         :ns-compile [main]
                         :uber-file uber-file
                         :exclude [#"(?i)^META-INF/license/.*"
                                   #"^license/.*"]})]
        (b/delete {:path class-dir})
        ;; no src or resources to copy
        (println "\nCompiling" (str main "..."))
        (b/compile-clj opts)
        (println "Building uberjar" (str uber-file "..."))
        (b/uber opts)
        (b/delete {:path class-dir})
        (println "Uberjar is built.")
        opts))))

(defn version
  "Prints the current version"
  [opts]
  (println version/name))

(defn install
  "Create and locally install library JAR files for the Polylith project

   Options:
   * :project - required, the name of the project to build"
  [{:keys [project] :as opts}]
  (let [project-opts (assoc opts
                            :project project
                            :installer (get opts :installer :local))]
    (println (str "Starting install for " project " project."))
    (-> project-opts
        (jar)
        (set/rename-keys {:jar-file :artifact})
        (d/deploy))
    (println (str "Local install completed for " project " project."))))

(defn deploy
  "Create and deploy library JAR files for the Polylith project.

   Currently, creates 'poly'.

   You can do a dry run by passing :installer :local which will
   deploy the JARs into your local Maven cache instead of to Clojars."
  [opts]
  (let [changed (changed-projects)
        projects-to-process (filter #{"poly"} changed)]
    (when-not (seq projects-to-process)
      (throw (ex-info "Cannot deploy projects. No projects have changed."
                      {:changed changed})))
    (doseq [project projects-to-process]
      (let [project-opts (assoc opts
                                :project project
                                :installer (get opts :installer :remote))]
        (println (str "Starting deployment for " project " project."))
        (-> project-opts
            (jar)
            (set/rename-keys {:jar-file :artifact})
            (d/deploy))
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
        (b/copy-file {:src (str "projects/" project "/" uber-file)
                      :target artifact-path})
        (b/copy-file {:src artifact-path
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
