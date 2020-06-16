(ns polylith.change.interface
  (:require [polylith.change.core :as core]))

(defn with-changes
  ([workspace]
   (core/with-changes workspace nil nil))
  ([workspace hash1]
   (core/with-changes workspace hash1 nil))
  ([workspace hash1 hash2]
   (core/with-changes workspace hash1 hash2)))
