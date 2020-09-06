(ns polylith.clj.core.poly-cli.core
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.util.interface.exception :as ex]
            [polylith.clj.core.user-input.interface :as user-input])
  (:gen-class))

(defn -main [& args]
  (let [user-input (user-input/extract-params args)
        {:keys [exit-code exception]} (command/execute-command user-input)]
      (when (-> exception nil? not)
        (ex/print-exception exception))
      (System/exit exit-code)))
