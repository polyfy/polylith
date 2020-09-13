(ns polylith.clj.core.deployer-cli.core
  (:require [polylith.clj.core.deployer.interface :as deployer])
  (:gen-class))

(defn -main [& args]
  (try
    (deployer/deploy)
    (System/exit 0)
    (catch Throwable t
      (let [message (.getMessage t)
            data (ex-data t)]
        (when message
          (println message))
        (when data
          (println data)))
      (System/exit 1))))
