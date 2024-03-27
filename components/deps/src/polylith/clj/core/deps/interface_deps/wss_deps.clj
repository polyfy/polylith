(ns ^:no-doc polylith.clj.core.deps.interface-deps.wss-deps
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.interface.interface :as interface]))

(defn ws-depends-on-interface [suffixed-top-ns interface-names ws-alias imported-ns]
  (let [{:keys [root-ns]} (common/extract-namespace suffixed-top-ns imported-ns)]
    (when (contains? interface-names root-ns)
      (str ws-alias "/" root-ns))))

(defn ws-depends-on-interfaces [suffixed-top-ns interface-names ws-alias {:keys [imports]}]
  (filterv identity (map #(ws-depends-on-interface suffixed-top-ns interface-names ws-alias (str %))
                         imports)))

(defn ws-bricks-depends-on-interfaces [suffixed-top-ns interface-names brick-namespaces ws-alias]
  (vec (mapcat #(ws-depends-on-interfaces suffixed-top-ns interface-names ws-alias %)
               brick-namespaces)))

(defn source-depends-on-interfaces-for-ws [{:keys [alias settings components]} namespaces source]
  (let [top-namespace (:top-namespace settings)
        suffixed-top-ns (common/suffix-ns-with-dot top-namespace)
        interfaces (interface/calculate components)
        interface-names (into (sorted-set) (keep :name) interfaces)]
    (ws-bricks-depends-on-interfaces suffixed-top-ns interface-names (source namespaces) alias)))
