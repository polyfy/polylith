(ns polylith.clj.cli-tool.test-runner.interfc
  (:require [polylith.clj.cli-tool.test-runner.core :as core]))

(defn run [workspace env]
  "Executes tests for the given environment or all tests if not given."
  (core/run workspace env))
