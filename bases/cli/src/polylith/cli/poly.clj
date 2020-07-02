(ns polylith.cli.poly
  (:require [polylith.cli.cmd.check :as check]
            [polylith.cli.cmd.help :as help]
            [polylith.cli.cmd.ws :as info]
            [polylith.change.interface :as change]
            [polylith.file.interface :as file]
            [polylith.test-runner.interface :as test-runner]
            [polylith.workspace.interface :as ws]
            [polylith.workspace-clj.interface :as ws-clj])
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
