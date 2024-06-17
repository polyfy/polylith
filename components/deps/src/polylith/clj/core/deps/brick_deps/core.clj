(ns ^:no-doc polylith.clj.core.deps.brick-deps.core
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn brick-dep [brick-id from-ns suffixed-top-ns base-names imported-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns imported-ns)
        type (if (contains? base-names root-ns) "base" "component")]
    (when (and root-ns
               (not= brick-id root-ns))
      {:to-brick-id root-ns
       :from-ns from-ns
       :to-type type
       :to-namespace depends-on-ns})))

(defn brick-ns-import-deps [brick-id suffixed-top-ns base-names {:keys [name imports]}]
  (map #(brick-dep brick-id name suffixed-top-ns base-names (str %))
       imports))

(defn brick-ns-deps [brick-id suffixed-top-ns base-names namespaces]
  (vec (mapcat #(brick-ns-import-deps brick-id suffixed-top-ns base-names %)
               namespaces)))

(defn brick-deps-for-source [suffixed-top-ns {:keys [name interface namespaces]} base-names source]
  (let [brick-id (or (:name interface) name)]
    (filter :to-brick-id
            (brick-ns-deps brick-id suffixed-top-ns base-names (source namespaces)))))

(defn valid-interface? [{:keys [to-brick-id to-namespace]} interface-names interface-ns]
  (and (contains? interface-names to-brick-id)
       (common/interface-ns? to-namespace interface-ns)))

(defn deps-for-source [suffixed-top-ns interface-names base-names {:keys [type] :as brick} interface-ns source]
  (let [deps (brick-deps-for-source suffixed-top-ns brick base-names source)
        dep-ids (set (map :to-brick-id deps))
        interface-deps (if (= :test source)
                         (set/intersection dep-ids interface-names)
                         (set (map :to-brick-id
                                   (filter #(valid-interface? % interface-names interface-ns)
                                           deps))))
        base-deps (set/intersection dep-ids base-names)
        valid-deps (if (= "component" type)
                     interface-deps
                     (set/union interface-deps base-deps))
        illegal-deps (filterv #(not (contains? valid-deps (:to-brick-id %)))
                              deps)]
    [(vec (sort interface-deps))
     (vec (sort base-deps))
     illegal-deps]))

(defn brick-deps
  "Returns the interface and base dependencies for a brick (component or base)."
  [suffixed-top-ns interface-names base-names interface-ns {:keys [type] :as brick}]
  (let [[ifc-src-deps base-src-deps illegal-deps] (deps-for-source suffixed-top-ns interface-names base-names brick interface-ns :src)
        [ifc-test-deps base-test-deps] (deps-for-source suffixed-top-ns interface-names base-names brick interface-ns :test)]
    (cond-> {:interface-deps {:src ifc-src-deps
                              :test ifc-test-deps}}
            (= "base" type) (assoc :base-deps {:src base-src-deps
                                               :test base-test-deps})
            (seq illegal-deps) (assoc :illegal-deps illegal-deps))))
