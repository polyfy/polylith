(ns ^:no-doc polylith.clj.core.test.interface
  (:require [polylith.clj.core.test.core :as core]))

(defn with-test-calculations
  "Make sure to first call changes/with-changes before calling this function."
  [workspace]
  (core/with-test-calculations workspace))
