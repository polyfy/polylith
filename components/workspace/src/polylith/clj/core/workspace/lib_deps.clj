(ns polylith.clj.core.workspace.lib-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]))

(defn included-in-ns? [lib-ns namespace]
  (or (= namespace lib-ns)
      (str/starts-with? namespace (common/suffix-ns-with-dot lib-ns))))

(defn included-ns [used-ns ns-libs]
  (util/find-first #(included-in-ns? % used-ns) ns-libs))

(defn deps [{:keys [top-namespace ns->lib]} {:keys [namespaces-src]}]
  (let [ns-libs (reverse (sort (map #(-> % first str) ns->lib)))
        used-namespaces (set (filter #(not (included-in-ns? top-namespace %))
                                     (mapcat :imports namespaces-src)))]
    (vec (sort (set (filter identity (map #(included-ns % ns-libs) used-namespaces)))))))
