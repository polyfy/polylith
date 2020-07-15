(ns polylith.clj.core.cli.poly
  (:require [polylith.clj.core.command.interfc :as command]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.workspace.interfc :as ws])
  (:gen-class))

(defn -main [& [cmd arg1 arg2]]
  (let [ws-path (file/absolute-path "")
        workspace (-> ws-path
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (try
      (command/execute-command workspace cmd arg1 arg2)
      (catch Exception e
        (println (or (-> e ex-data :err) (.getMessage e)))
        (System/exit (or (-> e ex-data :exit-code) 1)))
      (finally
        (System/exit 0)))))
