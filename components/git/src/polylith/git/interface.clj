(ns polylith.git.interface
  (:require [polylith.git.core :as core]))

(defn diff
  ([]
   (core/diff nil nil))
  ([hash1]
   (core/diff hash1 nil))
  ([hash1 hash2]
   (core/diff hash1 hash2)))
