(ns ^:no-doc polylith.clj.core.lib.ns-to-lib
  (:require [clojure.string :as str]
            [polylith.clj.core.lib.size :as size]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.common.interface :as common]))

(defn included-in-ns? [lib-ns namespace]
  (or (= namespace lib-ns)
      (str/starts-with? namespace (common/suffix-ns-with-dot lib-ns))))

(defn included-nss [used-ns ns-libs]
  (util/find-first #(included-in-ns? % used-ns) ns-libs))

(defn included-namespaces [top-namespace ns-to-lib namespaces]
  (let [ns-libs (reverse (sort (map #(-> % first str) ns-to-lib)))
        used-namespaces (set (filter #(not (included-in-ns? top-namespace %))
                                     (mapcat :imports namespaces)))]
    (set (filter identity (map #(included-nss % ns-libs) used-namespaces)))))

(defn with-version [ws-dir included-ns ns-to-lib lib->deps user-home]
  (let [lib-name (ns-to-lib included-ns)
        version (lib->deps lib-name)]
    (when lib-name
      (if version
        (or (size/with-size ws-dir [lib-name version] nil user-home)
            [lib-name {}])
        [lib-name {}]))))

(defn lib-deps [ws-dir top-namespace ns-to-lib lib->deps namespaces user-home]
  (let [included-nss (included-namespaces top-namespace ns-to-lib namespaces)]
    (into {} (set (map #(with-version ws-dir % ns-to-lib lib->deps user-home)
                       included-nss)))))
