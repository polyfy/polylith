(ns polylith.workspace.interface-deps
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

(defn error-message [{:keys [ns-path depends-on-interface depends-on-ns]} type]
  (when ns-path
    (str "Illegal dependency on namespace '" depends-on-interface "." depends-on-ns "' in '" type "s/" ns-path
         "'. Import '" depends-on-interface ".interface' instead to solve the problem.")))

(defn with-deps [top-ns {:keys [interface imports] :as brick} interface-names]
  "Takes incoming brick and returns a pimped brick with dependencies."
  (let [interface-name (:name interface)
        deps (brick-dependencies top-ns interface-name (set interface-names) imports)
        interface-deps (vec (sort (set (map :depends-on-interface deps))))]
    (assoc brick :dependencies interface-deps)))

(defn errors [top-ns {:keys [interface type imports]} interface-names errors]
  "Checks for dependencies to component interface namespaces other than 'interface'."
  (let [interface-name (:name interface)
        deps (brick-dependencies top-ns interface-name (set interface-names) imports)
        error-messages (filterv identity (map #(error-message % type)
                                              (filterv #(not= "interface" (:depends-on-ns %)) deps)))]
    (vec (concat errors error-messages))))
