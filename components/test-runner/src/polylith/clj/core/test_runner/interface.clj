(ns polylith.clj.core.test-runner.interface
  (:require [polylith.clj.core.test-runner.core :as core]))

(defn run [workspace]
  (core/run workspace))
