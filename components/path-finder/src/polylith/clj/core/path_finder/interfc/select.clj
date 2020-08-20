(ns polylith.clj.core.path-finder.interfc.select
  (:require [polylith.clj.core.path-finder.interfc.match :as m]))

(defn entries [path-entries & criterias]
  (m/filter-entries path-entries criterias))

(defn exists? [path-entries & criterias]
  (-> (m/filter-entries path-entries criterias) empty? not))

(defn lib-deps [dep-entries & criterias]
  (into {} (map :lib-dep (m/filter-entries dep-entries criterias))))

(defn paths [path-entries & criterias]
  (vec (sort (set (map :path (m/filter-entries path-entries criterias))))))

(defn names [path-entries & criterias]
  (vec (sort (set (map :name (m/filter-entries path-entries criterias))))))
