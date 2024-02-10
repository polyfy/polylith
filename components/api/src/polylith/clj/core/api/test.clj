(ns ^:no-doc polylith.clj.core.api.test
  (:require [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.test-runner-orchestrator.interface :as test-runner-orchestrator]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.workspace.interface :as ws])
  (:refer-clojure :exclude [test]))

(defn run-tests [user-input]
  (-> user-input
      ws-clj/workspace-from-disk
      ws/enrich-workspace
      change/with-changes
      (test-runner-orchestrator/run false "dark"))
  {:ok? true})

(defn print-argument-error [message]
  (println message)
  {:ok? false})

(defn test [args]
  (let [cmd-args (concat ["test"] args)
        user-input (user-input/extract-arguments cmd-args)
        unnamed-args (:unnamed-args user-input)
        {:keys [ok? message]} (common/validate-args unnamed-args "test :project since:release")]
    (if ok?
      (run-tests user-input)
      (print-argument-error message))))
