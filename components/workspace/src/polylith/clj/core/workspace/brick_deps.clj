(ns polylith.clj.core.workspace.brick-deps
  (:require [clojure.set :as set]
            [clojure.walk :as w]
            [polylith.clj.core.util.interfc :as util]))

(defn brick-deps [brick brick->deps traversed path]
  (let [deps (brick->deps brick)]
    (if (or (empty? deps)
            (contains? traversed brick))
      (vec (rest (conj path brick)))
      (map #(brick-deps % brick->deps (conj traversed brick) (conj path brick))
           deps))))

(defn brick-deps-info [brick-name brick->deps brick->direct-ifc-deps]
  (let [paths (atom [])
        deps (brick-deps brick-name brick->deps #{} [])
        update-paths (fn [node] (if (vector? node)
                                  (swap! paths conj node)
                                  node))
        _ (w/postwalk update-paths deps)
        circular-deps (first (sort-by count (filter #(= brick-name (last %)) @paths)))
        direct-deps (set (brick->deps brick-name))
        indirect-deps (set/difference (-> @paths flatten set) direct-deps)]
    (util/ordered-map :circular (when circular-deps (vec (cons brick-name circular-deps)))
                      :direct (-> direct-deps sort vec)
                      :direct-ifc (-> brick-name brick->direct-ifc-deps sort vec)
                      :indirect (-> indirect-deps sort vec))))

(defn direct-ifc-deps [{:keys [interface-deps]} ifc->comp]
  (let [ifc-deps (filter #(-> % ifc->comp nil?) interface-deps)]
    (when (-> ifc-deps empty? not)
      (-> ifc-deps sort vec))))

(defn environment-deps [component-names base-names components bases]
  (let [bricks (concat components bases)
        ifc->comp (into {} (map (juxt #(-> % :interface :name) :name)
                                (filter #(contains? (set component-names) (:name %))
                                        components)))
        brick->deps (into {} (map (juxt :name #(filter identity (mapv ifc->comp (:interface-deps %))))
                                  bricks))
        brick->direct-ifc-deps (into {} (map (juxt :name #(direct-ifc-deps % ifc->comp)) bricks))]
    (into {} (mapv (juxt identity #(brick-deps-info % brick->deps brick->direct-ifc-deps))
                   (concat component-names base-names)))))
