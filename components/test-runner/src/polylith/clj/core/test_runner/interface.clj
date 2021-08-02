(ns polylith.clj.core.test-runner.interface
  (:require [polylith.clj.core.test-runner.core :as core]))

(defn run [workspace is-verbose color-mode]
  (core/run workspace is-verbose color-mode))
