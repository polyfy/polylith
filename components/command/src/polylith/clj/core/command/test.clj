(ns polylith.clj.core.command.test
  (:require [polylith.clj.core.test-runner.interface :as test-runner]
            [polylith.clj.core.common.interface :as common]))

(defn run [workspace unnamed-args is-verbose color-mode]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "test project:dev")]
    (if ok?
      (test-runner/run workspace is-verbose color-mode)
      (println message))))
