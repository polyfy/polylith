(ns polylith.clj.core.default-test-runner.interface
  (:require [polylith.clj.core.default-test-runner.core :as core]))

(defn make [opts]
  (core/make opts))
