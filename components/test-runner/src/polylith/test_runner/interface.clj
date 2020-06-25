(ns polylith.test-runner.interface
  (:require [polylith.test-runner.core :as core]))

(defn run [workspace env]
  "Executes tests for the given environment or all tests if not given."
  (if (nil? env)
    (core/run-tests workspace)
    (core/run-tests workspace env)))
