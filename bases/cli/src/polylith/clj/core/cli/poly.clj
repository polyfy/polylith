(ns polylith.clj.core.cli.poly
  (:require [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.exception :as ex]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj])
  (:gen-class))

(defn -main [& [cmd arg1 arg2 arg3]]
  (let [current-dir (file/current-dir)
        exists? (file/exists (str current-dir "/deps.edn"))
        test-settings (common/test-settings [arg1 arg2 arg3])
        workspace (when exists? (-> current-dir
                                    ws-clj/workspace-from-disk
                                    (ws/enrich-workspace test-settings)
                                    (change/with-changes test-settings)))
        {:keys [exit-code exception]} (command/execute-command current-dir workspace cmd arg1 arg2 arg3)]
      (when (-> exception nil? not)
        (ex/print-exception exception))
      (System/exit exit-code)))
