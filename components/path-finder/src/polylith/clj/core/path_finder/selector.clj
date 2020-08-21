(ns polylith.clj.core.path-finder.selector
  (:require [polylith.clj.core.path-finder.interfc.match :as match]))

(defn entries [path-entries criterias]
  (match/filter-entries path-entries criterias))

(defn exists? [path-entries criterias]
  (-> (match/filter-entries path-entries criterias) empty? not))

(defn lib-deps [dep-entries criterias]
  (into {} (map :lib-dep (match/filter-entries dep-entries criterias))))

(defn paths [path-entries criterias]
  (vec (sort (set (map :path (match/filter-entries path-entries criterias))))))

(defn names [path-entries criterias]
  (vec (sort (set (map :name (match/filter-entries path-entries criterias))))))
