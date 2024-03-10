(ns ^:no-doc polylith.clj.core.deps.brick-deps
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

(defn ->dependees-ifc [names test?]
  (mapv #(dependee % test? :yellow) names))

(defn ->dependees-brick [names test? brick->color]
  (mapv #(dependee % test? (brick->color %)) names))

(defn deps [project brick->color brick-name]
  (let [deps (:deps project)
        dependers (->dependers deps brick-name brick->color)
        brick-deps (deps brick-name)
        direct-src-names (-> :src brick-deps :direct set)
        direct-test-names (-> :test brick-deps :direct set)
        direct-src (->dependees-brick direct-src-names false brick->color)
        direct-test (->dependees-brick (set/difference direct-test-names direct-src-names) true brick->color)
        missing-ifc-and-bases-src-names (-> :src brick-deps :missing-ifc-and-bases :direct set)
        missing-ifc-and-bases-test-names (-> :test brick-deps :missing-ifc-and-bases :direct set)
        missing-ifc-and-bases-src (->dependees-ifc missing-ifc-and-bases-src-names false)
        missing-ifc-and-bases-test (->dependees-ifc (set/difference missing-ifc-and-bases-test-names missing-ifc-and-bases-src) true)
        dependees (sort-them (concat direct-src direct-test missing-ifc-and-bases-src missing-ifc-and-bases-test))]
    [dependers dependees]))
