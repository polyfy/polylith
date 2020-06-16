(ns polylith.test.interface
  (:require [polylith.test.core :as core]))

(defn run [workspace env]
  "Executes the tests in the workspace"
  (core/run-test workspace env))
