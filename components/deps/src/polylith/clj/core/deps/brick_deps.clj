(ns polylith.clj.core.deps.brick-deps
  (:require [clojure.set :as set]))

(defn depender [[depending-comp {:keys [direct]}] component]
  (when (contains? (set direct) component)
    depending-comp))

(defn ->dependers [{:keys [deps]} component]
  (vec (sort (filter identity (map #(depender % component) deps)))))

(defn with-color [brick-name brick->color]
  [brick-name (brick->color brick-name)])

(def sorter {:yellow 1
             :green 2
             :blue 3})

(defn sort-them [bricks]
  (vec (sort-by (juxt #(-> % second sorter) first) bricks)))

(defn deps [environment brick->color brick-name]
  (let [dependers (sort-them (map #(with-color % brick->color)
                                  (->dependers environment brick-name)))
        {:keys [direct direct-ifc]} ((:deps environment) brick-name)
        interfaces (sort-them (map vector direct-ifc (repeat :yellow)))
        components (sort-them (map #(with-color % brick->color) direct))
        dependees (vec (concat interfaces components))]
    {:dependers dependers
     :dependees dependees}))
