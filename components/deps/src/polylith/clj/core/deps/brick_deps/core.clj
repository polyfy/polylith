(ns ^:no-doc polylith.clj.core.deps.brick-deps.core
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn brick-dep [brick-id from-type from-ns suffixed-top-ns interface-names base-names imported-ns]
  (let [{:keys [root-ns depends-on-ns]} (common/extract-namespace suffixed-top-ns imported-ns)
        type (cond
               (contains? base-names root-ns) "base"
               (contains? interface-names root-ns) "interface"
               :else "library")]
    (when (and root-ns
               (not= brick-id root-ns))
      {:from-type from-type
       :from-ns from-ns
       :to-type type
       :to-brick-id root-ns
       :to-namespace depends-on-ns})))

(defn brick-ns-import-deps [brick-id type suffixed-top-ns interface-names base-names {:keys [name imports]}]
  (map #(brick-dep brick-id type name suffixed-top-ns interface-names base-names (str %))
       imports))

(defn brick-ns-deps [brick-id type suffixed-top-ns interface-names base-names namespaces]
  (vec (mapcat #(brick-ns-import-deps brick-id type suffixed-top-ns interface-names base-names %)
               namespaces)))

(defn brick-deps-for-source [suffixed-top-ns {:keys [name type interface namespaces]} interface-names base-names source]
  (let [brick-id (or (:name interface) name)]
    (filter :to-brick-id
            (brick-ns-deps brick-id type suffixed-top-ns interface-names base-names (source namespaces)))))

(defn interface-dep? [{:keys [to-type to-namespace]} interface-ns source]
  (and (= "interface" to-type)
       (or (= :test source)
           (common/interface-ns? to-namespace interface-ns))))

(defn illegal-dep? [{:keys [from-type to-type to-namespace]} interface-ns source]
  (or (and (= "component" from-type)
           (= "base" to-type)
           (= :src source))
      (and (= "interface" to-type)
           (= :src source)
           (not (common/interface-ns? to-namespace interface-ns)))))

(defn base? [{:keys [to-type]}]
  (= "base" to-type))

(defn unique-ids [deps]
  (vec (sort (set deps))))

(defn deps-for-source [suffixed-top-ns interface-names base-names brick interface-ns source]
  (let [deps (brick-deps-for-source suffixed-top-ns brick interface-names base-names source)
        interface-deps (map :to-brick-id
                            (filter #(interface-dep? % interface-ns source)
                                    deps))
        base-deps (set/intersection (map :to-brick-id
                                         (filter base? deps)))
        illegal-deps (filterv #(illegal-dep? % interface-ns source) deps)]
    [(unique-ids interface-deps)
     (unique-ids base-deps)
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
