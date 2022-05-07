(ns polylith.clj.core.path-finder.selector
  (:require [polylith.clj.core.path-finder.interface.criterias :as criterias]))

(defn entries [path-entries criterias]
  (criterias/filter-entries path-entries criterias))

(defn exists? [path-entries criterias]
  (criterias/has-entry? path-entries criterias))

(defn lib-deps [dep-entries criterias]
  (into {} (map :lib-dep) (criterias/filter-entries dep-entries criterias)))

(defn paths [path-entries criterias]
  (vec (into (sorted-set) (map :path) (criterias/filter-entries path-entries criterias))))

(defn names [path-entries criterias]
  (vec (into (sorted-set) (map :name) (criterias/filter-entries path-entries criterias))))
