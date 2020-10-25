(ns polylith.clj.core.deployer-cli.core
  (:require [polylith.clj.core.deployer.interface :as deployer])
  (:gen-class))

(defn -main [& args]
  (try
    (let [cmd (first args)]
      (case cmd
        "deploy" (deployer/deploy)
        "create-artifacts" (deployer/create-artifacts)
        (throw (Exception. "Invalid command. Please provide either one of: deploy, create-artifacts"))))
    (System/exit 0)
    (catch Exception e
      (let [message (.getMessage e)
            data (ex-data e)]
        (when message
          (println message))
        (when data
          (println data)))
      (System/exit 1))))
