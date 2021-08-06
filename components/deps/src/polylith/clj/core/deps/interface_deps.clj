(ns polylith.clj.core.deps.interface-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]))

(defn brick-namespace [namespace]
  (let [idx (str/index-of namespace ".")]
    (if (nil? idx)
      namespace
      (subs namespace 0 idx))))

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
  (filterv identity (map #(dependency suffixed-top-ns interface-name name interface-names (str %)) imports)))

(defn interface-ns-deps [suffixed-top-ns interface-name interface-names brick-namespaces]
  (vec (mapcat #(interface-ns-import-deps suffixed-top-ns interface-name interface-names %) brick-namespaces)))

(defn interface-deps
  "Returns the interface dependencies for a brick (component or base)."
  [suffixed-top-ns interface-names {:keys [interface namespaces]}]
  (let [interface-name (:name interface)
        src-deps (interface-ns-deps suffixed-top-ns interface-name interface-names (:src namespaces))
        test-deps (interface-ns-deps suffixed-top-ns interface-name interface-names (:test namespaces))]
    {:src (vec (sort (set (map :depends-on-interface src-deps))))
     :test (vec (sort (set (map :depends-on-interface test-deps))))}))
