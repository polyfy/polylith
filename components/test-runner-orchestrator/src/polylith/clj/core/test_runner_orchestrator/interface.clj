(ns polylith.clj.core.test-runner-orchestrator.interface
  (:require [polylith.clj.core.test-runner-orchestrator.core :as core]))

(defn run [workspace is-verbose color-mode]
  (core/run workspace is-verbose color-mode))
