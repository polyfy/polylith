(ns polylith.clj.core.lib.deps
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.common.interface :as common]))

(defn included-in-ns? [lib-ns namespace]
  (or (= namespace lib-ns)
      (str/starts-with? namespace (common/suffix-ns-with-dot lib-ns))))

(defn included-nss [used-ns ns-libs]
  (util/find-first #(included-in-ns? % used-ns) ns-libs))

(defn included-namespaces [top-namespace ns-to-lib namespaces-src]
  (let [ns-libs (reverse (sort (map #(-> % first str) ns-to-lib)))
        used-namespaces (set (filter #(not (included-in-ns? top-namespace %))
                                     (mapcat :imports namespaces-src)))]
    (vec (sort (set (filter identity (map #(included-nss % ns-libs) used-namespaces)))))))

(defn with-size [lib-name dev-lib-deps]
  (if (contains? dev-lib-deps lib-name)
    [lib-name (dev-lib-deps lib-name)]
    [lib-name {}]))

(defn lib-deps [top-namespace ns-to-lib namespaces dev-lib-deps]
  (let [included-nss (included-namespaces top-namespace ns-to-lib namespaces)]
    (into {} (map #(with-size % dev-lib-deps)
                  (set (map ns-to-lib included-nss))))))
