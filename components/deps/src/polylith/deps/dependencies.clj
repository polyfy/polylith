(ns polylith.deps.dependencies
  (:require [clojure.string :as str]))

(defn brick-namespace [namespace]
  (let [idx (str/index-of namespace ".")]
    (if (nil? idx)
      namespace
      (subs namespace 0 idx))))

(defn dependency [top-ns interface-name path interface-names imported-ns]
  (let [import (if (str/starts-with? imported-ns top-ns)
                 (subs imported-ns (count top-ns))
                 imported-ns)
        idx (when import (str/index-of import "."))]
    (if (or (nil? idx)
            (neg? idx))
      nil
      (let [root-ns (subs import 0 idx)
            sub-ns (brick-namespace (subs import (inc idx)))]
        (when (and (contains? interface-names root-ns)
                   (not= root-ns interface-name))
          {:ns-path path
           :depends-on-interface root-ns
           :depends-on-ns sub-ns})))))

(defn brick-ns-dependencies [top-ns interface-name interface-names {:keys [ns-path imports]}]
  (filterv identity (map #(dependency top-ns interface-name ns-path interface-names (str %)) imports)))

(defn brick-dependencies [top-ns interface-name interface-names brick-imports]
  (vec (mapcat #(brick-ns-dependencies top-ns interface-name interface-names %) brick-imports)))

(defn with-deps [top-ns interface-names {:keys [interface imports] :as brick}]
  "Takes incoming brick and returns a pimped brick with dependencies."
  (let [interface-name (:name interface)
        deps (brick-dependencies top-ns interface-name interface-names imports)
        interface-deps (vec (sort (set (map :depends-on-interface deps))))]
    (assoc brick :dependencies interface-deps)))
