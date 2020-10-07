(ns polylith.clj.core.lib.deps
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.common.interface :as common]))

(defn included-in-ns? [lib-ns namespace]
  (or (= namespace lib-ns)
      (str/starts-with? namespace (common/suffix-ns-with-dot lib-ns))))

(defn included-nss [used-ns ns-libs]
  (util/find-first #(included-in-ns? % used-ns) ns-libs))

(defn dependencies [{:keys [top-namespace ns-to-lib]} {:keys [namespaces-src]}]
  (let [ns-libs (reverse (sort (map #(-> % first str) ns-to-lib)))
        used-namespaces (set (filter #(not (included-in-ns? top-namespace %))
                                     (mapcat :imports namespaces-src)))
        included-namespaces (vec (sort (set (filter identity (map #(included-nss % ns-libs) used-namespaces)))))
        included-libs (vec (sort (set (map ns-to-lib included-namespaces))))]
    {:included-namespaces included-namespaces
     :included-libs included-libs}))
