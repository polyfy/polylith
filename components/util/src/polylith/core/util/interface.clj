(ns polylith.core.util.interface
  (:require [polylith.core.util.core :as core]))

(defn find-first [predicate sequence]
  (core/find-first predicate sequence))

(defn ordered-map [& keyvals]
  (core/ordered-map keyvals))
