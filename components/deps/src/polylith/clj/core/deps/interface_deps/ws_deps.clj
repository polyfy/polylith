(ns ^:no-doc polylith.clj.core.deps.interface-deps.ws-deps
  (:require [polylith.clj.core.common.interface :as common]))

(defn existing-interface? [interface-name root-ns interface-names]
  (and (contains? interface-names root-ns)
       (not= root-ns interface-name)))

(defn dependency [suffixed-top-ns interface-name brick-ns-name interface-names imported-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns imported-ns)]
    (when (existing-interface? interface-name root-ns interface-names)
      {:namespace            brick-ns-name
       :depends-on-interface root-ns
       :depends-on-ns        depends-on-ns})))

(defn interface-ns-import-deps [suffixed-top-ns interface-name interface-names {:keys [name imports]}]
  (filterv identity (map #(dependency suffixed-top-ns interface-name name interface-names (str %))
                         imports)))

(defn interface-ns-deps [suffixed-top-ns interface-name interface-names brick-namespaces]
  (vec (mapcat #(interface-ns-import-deps suffixed-top-ns interface-name interface-names %)
               brick-namespaces)))

(defn interface-deps [suffixed-top-ns interface-name interface-names brick-namespaces]
  (map :depends-on-interface
       (interface-ns-deps suffixed-top-ns interface-name interface-names brick-namespaces)))
