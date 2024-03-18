(ns ^:no-doc polylith.clj.core.test.interface
  (:require [polylith.clj.core.test.core :as core]))

(defn with-to-test
  "Make sure to first call changes/with-changes before calling this function."
  [workspace]
  (core/with-to-test workspace))
