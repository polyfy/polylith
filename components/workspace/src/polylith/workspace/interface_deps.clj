(ns polylith.workspace.interface-deps
  (:require [clojure.string :as str]))

(defn brick-namespace [namespace]
  (let [idx (str/index-of namespace ".")]
    (if (nil? idx)
      namespace
      (subs namespace 0 idx))))

(defn dependency [top-ns brick-name path interface-names imported-ns]
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
                   (not= root-ns brick-name))
          {:ns-path path
           :depends-on-interface root-ns
           :depends-on-ns sub-ns})))))

(defn brick-ns-dependencies [top-ns brick-name interface-names {:keys [ns-path imports]}]
  (filterv identity (map #(dependency top-ns brick-name ns-path interface-names (str %)) imports)))

(defn brick-dependencies [top-ns brick-name interface-names brick-imports]
  (vec (mapcat #(brick-ns-dependencies top-ns brick-name interface-names %) brick-imports)))

(defn error [{:keys [ns-path depends-on-interface depends-on-ns]} type]
  (when ns-path
    (str "Illegal dependency on namespace '" depends-on-interface "." depends-on-ns "' in '" type "s/" ns-path
         "'. Import '" depends-on-interface ".interface' instead to solve the problem.")))

(defn with-deps [top-ns {:keys [name imports] :as brick} interface-names]
  "Takes incoming brick and returns a pimped brick with dependencies + errors if any."
  (let [deps (brick-dependencies top-ns name (set interface-names) imports)
        interface-deps (vec (sort (set (map :depends-on-interface deps))))]
    (assoc brick :dependencies interface-deps)))

(defn errors [top-ns {:keys [name type imports]} interface-names errors]
  (let [deps (brick-dependencies top-ns name (set interface-names) imports)
        new-errors (filterv identity (map #(error % type)
                                          (filterv #(not= "interface" (:depends-on-ns %)) deps)))]
     (vec (concat errors new-errors))))
