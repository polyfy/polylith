(ns polylith.clj.core.path-finder.selector
  (:require [polylith.clj.core.path-finder.interfc.criterias :as criterias]))

(defn entries [path-entries criterias]
  (criterias/filter-entries path-entries criterias))

(defn exists? [path-entries criterias]
  (-> (criterias/filter-entries path-entries criterias) empty? not))

(defn lib-deps [dep-entries criterias]
  (into {} (map :lib-dep (criterias/filter-entries dep-entries criterias))))

(defn paths [path-entries criterias]
  (vec (sort (set (map :path (criterias/filter-entries path-entries criterias))))))

(defn names [path-entries criterias]
  (vec (sort (set (map :name (criterias/filter-entries path-entries criterias))))))
