(ns polylith.clj.core.command.test
  (:require [polylith.clj.core.test-runner.interface :as test-runner]
            [polylith.clj.core.common.interface :as common]))

(defn run [workspace unnamed-args]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "test env:dev")]
    (if ok?
      (test-runner/run workspace)
      (println message))))
