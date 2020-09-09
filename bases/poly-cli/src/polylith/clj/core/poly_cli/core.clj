(ns polylith.clj.core.poly-cli.core
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.util.interface.exception :as ex]
            [polylith.clj.core.user-input.interface :as user-input])
  (:gen-class))

(defn -main [& args]
  (let [input (user-input/extract-params args)]
    (try
      (-> input command/execute-command System/exit)
      (catch Exception e
        (ex/print-exception e)
        (System/exit 1)))))
