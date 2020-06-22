(ns polylith.util.interface
  (:require [polylith.util.core :as core]))

(defn find-first [predicate sequence]
  (core/find-first predicate sequence))

(defn ordered-map [& keyvals]
  (core/ordered-map keyvals))
