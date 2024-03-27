(ns ^:no-doc polylith.clj.core.deps.interface-deps.core
  (:require [polylith.clj.core.deps.interface-deps.ws-deps :as ws]
            [polylith.clj.core.deps.interface-deps.wss-deps :as wss]))

(defn interface-deps-for-source [suffixed-top-ns workspaces interface interface-names namespaces source]
  (let [interface-name (:name interface)
        ws-deps (ws/interface-deps suffixed-top-ns interface-name interface-names (source namespaces))
        wss-deps (mapcat #(wss/source-depends-on-interfaces-for-ws % namespaces source)
                         (filter :is-direct-dependency workspaces))]
    (vec (sort (set (concat ws-deps wss-deps))))))

(defn interface-deps
  "Returns the interface dependencies for a brick (component or base)."
  [suffixed-top-ns interface-names workspaces {:keys [interface namespaces]}]
  (let [src-deps (interface-deps-for-source suffixed-top-ns workspaces interface interface-names namespaces :src)
        test-deps (interface-deps-for-source suffixed-top-ns workspaces interface interface-names namespaces :test)]
    {:src src-deps
     :test test-deps}))
