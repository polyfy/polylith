(ns polylith.clj.core.deployer.core
  (:require [deps-deploy.deps-deploy :as deps-deploy]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.shell.interface :as shell]
            [polylith.clj.core.version.interface :as version]
            [clojure.string :as str])
  (:import (java.io File)))

(defn create-pom-xml [current-dir env]
  (let [partial-pom-path (str current-dir "/environments/" env "/partial_pom.xml")
        pom-path (str current-dir "/environments/" env "/pom.xml")]
    (when-not (file/exists partial-pom-path)
      (throw (Exception. (str env " environment does not have a pom.xml. Use clojure -Spom to generate one before deploying."))))
    (when (file/exists pom-path)
      (file/delete-file pom-path))
    (let [content (slurp partial-pom-path)
          updated-content (str/replace content #"VERSION" version/version)]
      (spit pom-path updated-content))))

(defn build-jar [current-dir env type]
  (println (str "Building " (name type) "..."))
  (try
    (shell/sh (if (= :uberjar type) "./build-uberjar.sh" "./build-skinny-jar.sh") env :dir (str current-dir "/scripts"))
    (catch Throwable t
      (throw (ex-info (str "Unable to build a " (name type) " for " env " environment.")
                      {:current-dir current-dir
                       :env         env
                       :type        type}
                      t))))
  (println (str (name type) " is built.")))

(defn deploy-env [current-dir env]
  (create-pom-xml current-dir env)
  (try
    (let [env-prefix (str (file/current-dir) "/environments/" env)
          coordinates (:coordinates (deps-deploy/coordinates-from-pom (slurp (str (file/current-dir) "/environments/" env "/pom.xml"))))
          artifact-map {[:extension "pom" :classifier nil] (str env-prefix "/pom.xml")
                        [:extension "jar" :classifier nil] (str env-prefix "/target/" env "-skinny.jar")}]
      (deps-deploy/deploy {:installer    :clojars
                           :coordinates  coordinates
                           :artifact-map artifact-map}))
    (catch Throwable t
      (throw (ex-info (str "Could not deploy " env " environment to clojars.")
                      {:current-dir current-dir
                       :env env}
                      t)))))

(def deployable-environments #{"poly" "migrator" "api"})

(defn deploy []
  (let [current-dir (file/current-dir)
        changed-environments (filter #(contains? deployable-environments %)
                                     (api/environments-to-deploy))]
    (when (empty? changed-environments)
      (throw (Exception. "Cannot deploy environments. None of the environments in this workspace changed.")))
    (doseq [env changed-environments]
      (println (str "Starting deployment for " env " environment."))
      (build-jar current-dir env :skinny-jar)
      (deploy-env current-dir env)
      (println (str "Deployment completed for " env " environment.")))))

(defn make-install-script [env]
  (str "#!/usr/bin/env bash\n\nprefix=\"$1\"\n\n# jar needed by scripts\nmkdir -p \"$prefix/libexec\"\ncp ./*.jar \"$prefix/libexec\"\n\n# scripts\n${HOMEBREW_RUBY_PATH} -pi.bak -e \"gsub(/PREFIX/, '$prefix')\" " env "\nmkdir -p \"$prefix/bin\"\ncp " env " \"$prefix/bin\"\n"))

(defn make-executable-script [env artifact-name]
  (str "#!/usr/bin/env bash\n\nset -e\n\n# Set dir containing the installed files\ninstall_dir=PREFIX\n" env "_jar=\"$install_dir/libexec/" artifact-name "\"\n\n# Find java executable\nset +e\nJAVA_CMD=$(type -p java)\nset -e\nif [[ -z \"$JAVA_CMD\" ]]; then\n  if [[ -n \"$JAVA_HOME\" ]] && [[ -x \"$JAVA_HOME/bin/java\" ]]; then\n    JAVA_CMD=\"$JAVA_HOME/bin/java\"\n  else\n    >&2 echo \"Couldn't find 'java'. Please set JAVA_HOME.\"\n    exit 1\n  fi\nfi\n\nexec \"$JAVA_CMD\" -jar \"$" env "_jar\" \"$@\"\n"))

(defn create-brew-package [^String artifacts-dir ^String env ^String artifact-name]
  (let [package-path (str artifacts-dir "/" env)
        package-dir (File. package-path)
        _ (.mkdirs package-dir)
        install-sh (File. package-dir "install.sh")
        executable (File. package-dir env)
        install-script (make-install-script env)
        executable-script (make-executable-script env artifact-name)]
    (spit install-sh install-script)
    (spit executable executable-script)
    (shell/sh "chmod" "+x" (.getAbsolutePath install-sh))
    (shell/sh "chmod" "+x" (.getAbsolutePath executable))
    (file/copy-file (str artifacts-dir "/" artifact-name)
                    (str artifacts-dir "/" env "/" artifact-name))
    (shell/sh "tar" "-pcvzf" (str/replace artifact-name #".jar" ".tar.gz") env :dir artifacts-dir)
    (file/delete-dir package-path)))

(def environments-to-deploy-brew #{"poly" "migrator"})

(defn create-artifacts []
  (let [current-dir (file/current-dir)
        artifacts-dir (str current-dir "/artifacts")]
    (when (file/exists artifacts-dir)
      (file/delete-dir artifacts-dir))
    (.mkdirs (File. artifacts-dir))
    (doseq [env environments-to-deploy-brew]
      (println (str "Creating artifacts for: " env))
      (let [jar-path (str current-dir "/environments/" env "/target/" env ".jar")
            artifact-name (str env "-" version/version ".jar")
            artifact-path (str artifacts-dir "/" artifact-name)]
        (build-jar current-dir env :uberjar)
        (file/copy-file jar-path artifact-path)
        (create-brew-package artifacts-dir env artifact-name)))))
