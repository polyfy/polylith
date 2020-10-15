(ns polylith.clj.core.deployer.core
  (:require [deps-deploy.deps-deploy :as deps-deploy]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.shell.interface :as shell]
            [polylith.clj.core.version.interface :as version]
            [clojure.string :as str])
  (:import (java.io File)))

(defn create-pom-xml [current-dir project-name]
  (let [partial-pom-path (str current-dir "/projects/" project-name "/partial_pom.xml")
        pom-path (str current-dir "/projects/" project-name "/pom.xml")]
    (when-not (file/exists partial-pom-path)
      (throw (Exception. (str project-name " project does not have a pom.xml. Use clojure -Spom to generate one before deploying."))))
    (when (file/exists pom-path)
      (file/delete-file pom-path))
    (let [content (slurp partial-pom-path)
          updated-content (str/replace content #"VERSION" version/version)]
      (spit pom-path updated-content))))

(defn build-jar [current-dir project-name type]
  (println (str "Building " (name type) "..."))
  (try
    (shell/sh (if (= :uberjar type) "./build-uberjar.sh" "./build-skinny-jar.sh") project-name :dir (str current-dir "/scripts"))
    (catch Exception e
      (throw (ex-info (str "Unable to build a " (name type) " for " project-name " project.")
                      {:current-dir  current-dir
                       :project-name project-name
                       :type         type}
                      e))))
  (println (str (name type) " is built.")))

(defn deploy-project [current-dir project-name]
  (create-pom-xml current-dir project-name)
  (try
    (let [project-prefix (str (file/current-dir) "/projects/" project-name)
          coordinates (:coordinates (deps-deploy/coordinates-from-pom (slurp (str (file/current-dir) "/projects/" project-name "/pom.xml"))))
          artifact-map {[:extension "pom" :classifier nil] (str project-prefix "/pom.xml")
                        [:extension "jar" :classifier nil] (str project-prefix "/target/" project-name "-skinny.jar")}]
      (deps-deploy/deploy {:installer    :clojars
                           :coordinates  coordinates
                           :artifact-map artifact-map}))
    (catch Throwable t
      (throw (ex-info (str "Could not deploy " project-name " project to clojars.")
                      {:current-dir current-dir
                       :project-name project-name}
                      t)))))

(def projects-to-deploy-clojars #{"poly" "poly-migrator" "api"})

(defn deploy []
  (let [current-dir (file/current-dir)
        changed-projects (filter #(contains? projects-to-deploy-clojars %)
                                 (api/projects-to-deploy))]
    (when (empty? changed-projects)
      (throw (Exception. "Cannot deploy projects. None of the projects in this workspace changed.")))
    (doseq [project-name changed-projects]
      (println (str "Starting deployment for " project-name " project."))
      (build-jar current-dir project-name :skinny-jar)
      (deploy-project current-dir project-name)
      (println (str "Deployment completed for " project-name " project.")))))

(defn make-install-script [project-name]
  (str "#!/usr/bin/env bash\n\n"

       "prefix=\"$1\"\n\n"

       "# jar needed by scripts\n"
       "mkdir -p \"$prefix/libexec\"\n"
       "cp ./*.jar \"$prefix/libexec\"\n\n"

       "# scripts\n"
       "${HOMEBREW_RUBY_PATH} -pi.bak -e \"gsub(/PREFIX/, '$prefix')\" " project-name "\n"
       "mkdir -p \"$prefix/bin\"\n"
       "cp " project-name " \"$prefix/bin\"\n"))

(defn make-executable-script [project-name artifact-name]
  (let [project (str/replace project-name #"-" "_")]
    (str "#!/usr/bin/env bash\n\n"

         "set -e\n\n"

         "# Set dir containing the installed files\n"
         "install_dir=PREFIX\n"
         project "_jar=\"$install_dir/libexec/" artifact-name "\"\n\n"

         "# Find java executable\n"
         "set +e\n"
         "JAVA_CMD=$(type -p java)\n"
         "set -e\n"
         "if [[ -z \"$JAVA_CMD\" ]]; then\n"
         "  if [[ -n \"$JAVA_HOME\" ]] && [[ -x \"$JAVA_HOME/bin/java\" ]]; then\n"
         "    JAVA_CMD=\"$JAVA_HOME/bin/java\"\n"
         "  else\n"
         "    >&2 echo \"Couldn't find 'java'. Please set JAVA_HOME.\"\n"
         "    exit 1\n"
         "  fi\n"
         "fi\n\n"

         "exec \"$JAVA_CMD\" -jar \"$" project "_jar\" \"$@\"\n")))

(defn get-sha-sum [file-path]
  (let [output (shell/sh "shasum" "-a" "256" file-path)]
    (first (str/split output #" "))))

(defn create-brew-package [^String artifacts-dir ^String project-name ^String artifact-name]
  (let [package-path (str artifacts-dir "/" project-name)
        package-dir (File. package-path)
        _ (.mkdirs package-dir)
        install-sh (File. package-dir "install.sh")
        executable (File. package-dir project-name)
        install-script (make-install-script project-name)
        executable-script (make-executable-script project-name artifact-name)
        tar-gz-name (str/replace artifact-name #".jar" ".tar.gz")
        shasum (File. artifacts-dir (str tar-gz-name ".sha1"))]
    (spit install-sh install-script)
    (spit executable executable-script)
    (shell/sh "chmod" "+x" (.getAbsolutePath install-sh))
    (shell/sh "chmod" "+x" (.getAbsolutePath executable))
    (file/copy-file (str artifacts-dir "/" artifact-name)
                    (str artifacts-dir "/" project-name "/" artifact-name))
    (shell/sh "tar" "-pcvzf" tar-gz-name project-name :dir artifacts-dir)
    (let [shasum-content (get-sha-sum (str artifacts-dir "/" tar-gz-name))]
      (println (str "Shasum for " project-name ": " shasum-content))
      (spit shasum shasum-content))
    (file/delete-dir package-path)))

(def projects-to-deploy-as-artifacts #{"poly" "poly-migrator"})

(defn create-artifacts []
  (let [current-dir (file/current-dir)
        changed-projects (filter #(contains? projects-to-deploy-as-artifacts %)
                                 (api/projects-to-deploy))]
    (when (empty? changed-projects)
      (throw (Exception. "Cannot create artifacts for project. None of the projects in this workspace changed.")))
    (let [artifacts-dir (str current-dir "/artifacts")]
      (when (file/exists artifacts-dir)
        (file/delete-dir artifacts-dir))
      (.mkdirs (File. artifacts-dir))
      (doseq [project-name projects-to-deploy-as-artifacts]
        (println (str "Creating artifacts for: " project-name))
        (let [jar-path (str current-dir "/projects/" project-name "/target/" project-name ".jar")
              artifact-name (str project-name "-" version/version ".jar")
              artifact-path (str artifacts-dir "/" artifact-name)]
          (build-jar current-dir project-name :uberjar)
          (file/copy-file jar-path artifact-path)
          (create-brew-package artifacts-dir project-name artifact-name))))))
