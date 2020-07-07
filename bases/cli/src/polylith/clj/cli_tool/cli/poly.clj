(ns polylith.clj.cli-tool.cli.poly
  (:require [polylith.clj.cli-tool.cli.cmd.check :as check]
            [polylith.clj.cli-tool.cli.cmd.help :as help]
            [polylith.clj.cli-tool.cli.cmd.info :as info]
            [polylith.clj.cli-tool.test-runner.interfc :as test-runner]
            [polylith.clj.cli-tool.workspace-clj.interfc :as ws-clj]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.workspace.interfc :as ws])
  (:gen-class))

(defn -main [& [cmd arg]]
  (let [ws-path (file/absolute-path "")
        workspace (-> ws-path
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)
        color-mode (-> workspace :settings :color-mode)]
    (try
      (case cmd
        "check" (check/execute workspace)
        "help" (help/execute color-mode)
        "info" (info/execute workspace arg)
        "test" (test-runner/run workspace arg)
        (help/execute color-mode))
      (catch Exception e
        (println (or (-> e ex-data :err) (.getMessage e)))
        (System/exit (or (-> e ex-data :exit-code) 1)))
      (finally
        (System/exit 0)))))
