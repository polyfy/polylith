(ns ^:no-doc polylith.clj.core.util.interface
  (:require [polylith.clj.core.util.core :as core]
            [polylith.clj.core.util.edn-sanitizer :as edn-sanitizer]))

(defn find-first [predicate sequence]
  (core/find-first predicate sequence))

(defn find-first-index [predicate sequence]
  (core/find-first-index predicate sequence))

(defn ordered-map [& keyvals]
  (core/ordered-map keyvals))

(defn stringify-and-sort-map [m]
  (core/stringify-and-sort-map m))

(defn sort-map [m]
  (core/sort-map m))

(defn first-as-vector [vals]
  (if (empty? vals)
    []
    [(first vals)]))

(defn sanitize-keywords [edn]
  (edn-sanitizer/sanitize-keywords edn))

(defmacro def-keys
  "Defines all selected keys in a map, e.g.:
    (def-map mymap [name age length])"
  [amap keys]
  `(core/def-map ~amap ~keys))

(defn rf-some
  "Reducing function that returns the first logical true value.
  Ported from net.cgrand/xforms"
  ([] nil)
  ([x] x)
  ([_ x] (when x (reduced x))))

(defn xf-some
  "Process coll through the specified xform and returns the first logical true value.
  Ported from net.cgrand/xforms"
  [xform coll]
  (transduce xform rf-some nil coll))
