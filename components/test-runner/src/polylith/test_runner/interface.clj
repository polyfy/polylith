(ns polylith.test-runner.interface
  (:require [polylith.test-runner.core :as core]))

(defn run [workspace env]
  "Executes the tests from the given environment."
  (core/run-tests workspace env))
