(ns polylith.core.cli.poly
  (:require [polylith.core.cli.cmd.check :as check]
            [polylith.core.cli.cmd.help :as help]
            [polylith.core.cli.cmd.ws :as info]
            [polylith.core.change.interface :as change]
            [polylith.core.file.interface :as file]
            [polylith.core.test-runner.interface :as test-runner]
            [polylith.core.workspace.interface :as ws]
            [polylith.core.workspace-clj.interface :as ws-clj])
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
        "test" (test-runner/run workspace arg)
        "ws" (info/execute workspace arg)
        (help/execute color-mode))
      (catch Exception e
        (println (or (-> e ex-data :err) (.getMessage e)))
        (System/exit (or (-> e ex-data :exit-code) 1)))
      (finally
        (System/exit 0)))))
