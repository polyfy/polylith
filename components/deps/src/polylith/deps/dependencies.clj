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
    (when (not (or (nil? idx)
                   (neg? idx)))
      (let [root-ns (subs import 0 idx)
            brick-ns (brick-namespace (subs import (inc idx)))]
        (when (and (contains? interface-names root-ns)
                   (not= root-ns interface-name))
          {:namespace path
           :depends-on-interface root-ns
           :depends-on-ns brick-ns})))))

(defn brick-ns-dependencies [top-ns interface-name interface-names {:keys [name imports]}]
  (filterv identity (map #(dependency top-ns interface-name name interface-names (str %)) imports)))

(defn brick-dependencies [top-ns interface-name interface-names brick-namespaces]
  (vec (mapcat #(brick-ns-dependencies top-ns interface-name interface-names %) brick-namespaces)))

(defn interface-deps [top-ns interface-names {:keys [interface src-namespaces]}]
  "Returns the interface dependencies for a brick (component or base)."
  (let [interface-name (:name interface)
        deps (brick-dependencies top-ns interface-name interface-names src-namespaces)]
    (vec (sort (set (map :depends-on-interface deps))))))
