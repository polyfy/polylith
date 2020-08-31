(ns polylith.clj.core.poly-cli.poly
  (:require [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.user-input.interfc :as user-input]
            [polylith.clj.core.util.interfc.exception :as ex])
  (:gen-class))

(defn -main [& args]
  (let [user-input (user-input/extract-params args)
        {:keys [exit-code exception]} (command/execute-command user-input)]
      (when (-> exception nil? not)
        (ex/print-exception exception))
      (System/exit exit-code)))
