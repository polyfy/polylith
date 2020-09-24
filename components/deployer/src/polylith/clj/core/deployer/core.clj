(ns polylith.clj.core.deployer.core
  (:require [deps-deploy.deps-deploy :as deps-deploy]
            [polylith.clj.core.api.interface :as api]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.shell.interface :as shell]))

(defn check-pom-xml [current-dir env]
  (when-not (file/exists (str current-dir "/environments/" env "/pom.xml"))
    (throw (Exception. (str env " environment does not have a pom.xml. Use clojure -Spom to generate one before deploying.")))))

(defn build-jar [current-dir env]
  (println "Building uberjar...")
  (try
    (shell/sh "./build-uberjar.sh" env :dir (str current-dir "/scripts"))
    (catch Throwable t
      (throw (ex-info (str "Unable to build a uberjar for " env " environment.")
                      {:current-dir current-dir
                       :env         env}
                      t))))
  (println "Uberjar is built."))

(defn deploy-env [current-dir env]
  (check-pom-xml current-dir env)
  (try
    (let [env-prefix (str (file/current-dir) "/environments/" env)
          coordinates (:coordinates (deps-deploy/coordinates-from-pom (slurp (str (file/current-dir) "/environments/" env "/pom.xml"))))
          artifact-map {[:extension "pom" :classifier nil] (str env-prefix "/pom.xml")
                        [:extension "jar" :classifier nil] (str env-prefix "/target/" env ".jar")}]
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
      (build-jar current-dir env)
      (deploy-env current-dir env)
      (println (str "Deployment completed for " env " environment.")))))
