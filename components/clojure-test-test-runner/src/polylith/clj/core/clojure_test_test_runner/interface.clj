(ns polylith.clj.core.clojure-test-test-runner.interface
  (:require [polylith.clj.core.clojure-test-test-runner.core :as core]))

(defn create [opts]
  (core/create opts))
