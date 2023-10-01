(ns ^:no-doc polylith.clj.core.command.test
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.test-runner-orchestrator.interface :as test-runner-orchestrator]))

(defn run
  "Return true if the tests could be executed correctly."
  [workspace unnamed-args test-result is-verbose color-mode]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "test project:dev")]
    (if ok?
      (reset! test-result
              (test-runner-orchestrator/run workspace is-verbose color-mode))
      (println message))))
