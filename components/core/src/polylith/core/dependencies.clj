(ns polylith.core.dependencies
  (:require [clojure.string :as str]))

(defn brick-namespace [namespace]
  (let [idx (str/index-of namespace ".")]
    (if (nil? idx)
      namespace
      (subs namespace 0 idx))))

(defn dependency [top-ns brick path interface-names imported-ns]
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
                   (not= root-ns brick))
          {:ns-path path
           :depends-on-interface root-ns
           :depends-on-ns sub-ns})))))

(defn brick-ns-dependencies [top-ns brick interface-names {:keys [ns-path imports]}]
  (filterv identity (map #(dependency top-ns brick ns-path interface-names (str %)) imports)))

(defn brick-dependencies [top-ns brick interface-names brick-imports]
  (vec (mapcat #(brick-ns-dependencies top-ns brick interface-names %) brick-imports)))

(defn dependencies [top-ns brick interface-names brick-imports]
  (let [deps (brick-dependencies top-ns brick interface-names brick-imports)]
    {:interfaces (vec (sort (set (map :depends-on-interface deps))))
     :illegal-deps (filterv #(not= "interface" (:depends-on-ns %)) deps)}))
