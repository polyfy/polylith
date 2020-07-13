(ns polylith.clj.core.util.interfc
  (:require [polylith.clj.core.util.core :as core]))

(defn find-first [predicate sequence]
  (core/find-first predicate sequence))

(defn ordered-map [& keyvals]
  (core/ordered-map keyvals))

(defn first-as-vector [vals]
  (if (empty? vals)
    []
    [(first vals)]))

(defmacro def-map [amap keys]
  "Defines all selected keys in a map, e.g.:
    (def-map mymap [name age length])"
  `(core/def-map ~amap ~keys))
