(ns polylith.clj.core.cli.poly
  (:require [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj])
  (:gen-class))

(defn -main [& [cmd arg1 arg2]]
  (let [ws-dir (file/current-dir)
        exists? (file/exists (str ws-dir "/deps.edn"))
        workspace (when exists? (-> ws-dir
                                    ws-clj/workspace-from-disk
                                    ws/enrich-workspace
                                    change/with-changes))
        {:keys [exit-code exception]} (command/execute-command ws-dir workspace cmd arg1 arg2)]
      (when (-> exception nil? not)
        (ex/print-exception exception))
      (System/exit exit-code)))
