(ns polylith.clj.core.deps.brick-deps
  (:require [clojure.set :as set]))

(defn suffixed-name [name test?]
  (str name (if test? " (t)" "")))

(defn colored-brick [brick-name brick->color test?]
  [(suffixed-name brick-name test?)
   (brick->color brick-name)])

(defn depender [[depending-brick {:keys [src test]}] brick-name brick->color]
  (cond
    (contains? (-> src :direct set) brick-name) (colored-brick depending-brick brick->color false)
    (contains? (-> test :direct set) brick-name) (colored-brick depending-brick brick->color true)))

(def sorter {:yellow 1
             :green 2
             :blue 3})

(defn sort-them [bricks]
  (vec (sort-by (juxt #(-> % second sorter) first) bricks)))

(defn ->dependers [deps brick-name brick->color]
  (sort-them (filter identity (map #(depender % brick-name brick->color)
                                   deps))))

(defn dependee [name test? color]
  [(suffixed-name name test?) color])

(defn ->dependees [names test? color]
  (mapv #(dependee % test? color) names))

(defn deps [project brick->color brick-name]
  (let [deps (:deps project)
        dependers (->dependers deps brick-name brick->color)
        brick-deps (deps brick-name)
        direct-src-names (-> :src brick-deps :direct set)
        direct-test-names (-> :test brick-deps :direct set)
        direct-src (->dependees direct-src-names false :green)
        direct-test (->dependees (set/difference direct-test-names direct-src-names) true :green)
        missing-ifc-src-names (-> :src brick-deps :missing-ifc :direct set)
        missing-ifc-test-names (-> :test brick-deps :missing-ifc :direct set)
        missing-ifc-src (->dependees missing-ifc-src-names false :yellow)
        missing-ifc-test (->dependees (set/difference missing-ifc-test-names missing-ifc-src) true :yellow)
        dependees (sort-them (concat direct-src direct-test missing-ifc-src missing-ifc-test))]
    [dependers dependees]))
