(ns polylith.change.interface
  (:require [polylith.change.core :as core]))

(defn changes
  ([]
   (core/changes))
  ([hash1]
   (core/changes hash1))
  ([hash1 hash2]
   (core/changes hash1 hash2)))
