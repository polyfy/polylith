(ns polylith.clj.core.deps.base-deps
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn base-name [namespace suffixed-top-ns]
  (:root-ns (common/extract-namespace suffixed-top-ns namespace)))

(defn source-deps [bases base suffixed-top-ns source]
  (let [bases-namespaces (set (common/entities-namespaces bases source))
        base-namespaces (common/entity-namespaces base source)
        base-imports (set (set (common/entity-imports base source)))
        other-namespaces (set/difference bases-namespaces base-namespaces)
        namespaces (vec (set/intersection base-imports other-namespaces))]
    (vec (sort (mapv #(base-name % suffixed-top-ns)
                     namespaces)))))

(defn base-deps [bases base suffixed-top-ns]
  {:src (source-deps bases base suffixed-top-ns :src)
   :test (source-deps bases base suffixed-top-ns :test)})
