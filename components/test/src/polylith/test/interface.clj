(ns polylith.test.interface
  (:require [polylith.test.core :as core]))

(defn run [workspace env]
  "Executes the tests from the given environment."
  (core/run-tests workspace env))
