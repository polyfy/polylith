(ns polylith.clj.core.cli.poly
  (:require [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj])
  (:gen-class))

(defn -main [& [cmd arg1 arg2 arg3]]
  (let [ws-root-path (file/current-path)
        exists? (file/exists (str ws-root-path "/deps.edn"))
        workspace (when exists? (-> ws-root-path
                                    ws-clj/workspace-from-disk
                                    ws/enrich-workspace
                                    change/with-changes))
        {:keys [ok? exception]} (command/execute-command ws-root-path workspace cmd arg1 arg2 arg3)]
      (when (not ok?)
        (ex/print-exception exception)
        (System/exit (or (-> exception ex-data :exit-code) 1)))
      (System/exit 0)))
