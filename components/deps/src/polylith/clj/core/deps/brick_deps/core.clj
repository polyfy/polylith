(ns ^:no-doc polylith.clj.core.deps.brick-deps.core
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn brick-dep [ws-suffixed-top-ns brick-id alias suffixed-top-ns imported-ns]
  (when-let [dep (:root-ns (common/extract-namespace suffixed-top-ns imported-ns))]
    (if (= ws-suffixed-top-ns suffixed-top-ns)
      (when (not= brick-id dep)
        dep)
      (str alias "/" dep))))

(defn brick-ns-import-deps [ws-suffixed-top-ns brick-id alias suffixed-top-ns {:keys [imports]}]
  (map #(brick-dep ws-suffixed-top-ns brick-id alias suffixed-top-ns (str %))
       imports))

(defn brick-ns-deps [ws-suffixed-top-ns brick-id [alias suffixed-top-ns] namespaces]
  (vec (mapcat #(brick-ns-import-deps ws-suffixed-top-ns brick-id alias suffixed-top-ns %)
               namespaces)))

(defn brick-deps-for-source [ws-alias suffixed-top-ns workspaces {:keys [name interface namespaces]} source]
  (let [brick-id (or (:name interface) name)
        alias-suffixed-nss (set (concat [[ws-alias suffixed-top-ns]]
                                        (map (juxt :alias #(-> % :settings :top-namespace common/suffix-ns-with-dot))
                                             workspaces)))]
    (filter identity
            (mapcat #(brick-ns-deps suffixed-top-ns brick-id % (source namespaces))
                    alias-suffixed-nss))))

(defn divided-deps-for-source [ws-alias suffixed-top-ns interface-names workspaces brick source]
  (let [deps (set (brick-deps-for-source ws-alias suffixed-top-ns workspaces brick source))
        interface-deps (vec (sort (set/intersection deps interface-names)))
        base-deps (vec (sort (set/difference deps interface-deps)))]
    [interface-deps base-deps]))

(defn brick-deps
  "Returns the interface and base dependencies for a brick (component or base)."
  [ws-alias suffixed-top-ns interface-names workspaces brick]
  (let [[ifc-src-deps base-src-deps] (divided-deps-for-source ws-alias suffixed-top-ns interface-names workspaces brick :src)
        [ifc-test-deps base-test-deps] (divided-deps-for-source ws-alias suffixed-top-ns interface-names workspaces brick :test)]
    {:base-deps {:src base-src-deps
                 :test base-test-deps}
     :interface-deps {:src ifc-src-deps
                      :test ifc-test-deps}}))
