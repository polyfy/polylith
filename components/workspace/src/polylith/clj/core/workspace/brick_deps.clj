(ns polylith.clj.core.workspace.brick-deps
  (:require [clojure.set :as set]
            [clojure.walk :as w]
            [polylith.clj.core.util.interfc :as util]))

(defn brick-deps [brick brick->deps ifc->comp traversed path]
  (let [deps (brick->deps brick)]
    (if (or (empty? deps)
            (contains? traversed brick))
      (vec (rest (conj path brick)))
      (map #(brick-deps % brick->deps ifc->comp (conj traversed brick) (conj path brick))
           deps))))

(defn brick-deps-info [brick brick->deps ifc->comp]
  (let [paths (atom [])
        deps (brick-deps brick brick->deps ifc->comp #{} [])
        update-paths (fn [node] (if (vector? node)
                                  (swap! paths conj node)
                                  node))
        _ (w/postwalk update-paths deps)
        circular-deps (first (sort-by count (filter #(= brick (last %)) @paths)))
        direct-deps (set (brick->deps brick))
        indirectly-deps (set/difference (-> @paths flatten set) direct-deps)]
    (util/ordered-map :circular (when circular-deps (vec (cons brick circular-deps)))
                      :direct (-> direct-deps sort vec)
                      :indirect (-> indirectly-deps sort vec))))

(defn environment-deps [{:keys [name component-names]} components bases]
  (let [ifc->comp (into {} (map (juxt #(-> % :interface :name) :name)
                                (filter #(contains? (set component-names) (:name %))
                                        components)))
        brick->deps (into {} (map (juxt :name #(filter identity (mapv ifc->comp (:interface-deps %))))
                                  (concat components bases)))
        brick-names (map :name (concat components bases))
        brick->deps (into {} (mapv (juxt identity #(brick-deps-info % brick->deps ifc->comp)) brick-names))]
    [name brick->deps]))

(defn env->brick-deps [environments components bases]
  (into {} (mapv #(environment-deps % components bases)
                 environments)))
