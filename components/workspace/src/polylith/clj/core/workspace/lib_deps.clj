(ns polylith.clj.core.workspace.lib-deps
  (:require [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]))

(defn included-in-ns? [lib-ns namespace]
  (or (= namespace lib-ns)
      (str/starts-with? namespace (common/suffix-ns-with-dot lib-ns))))

(defn included-lib [used-ns ns-libs ns->lib]
  (ns->lib (util/find-first #(included-in-ns? % used-ns) ns-libs)))

(defn dep-names [{:keys [top-namespace ns->lib]} {:keys [namespaces-src]}]
  (let [ns-libs (reverse (sort (map #(-> % first str) ns->lib)))
        used-namespaces (set (filter #(not (included-in-ns? top-namespace %))
                                     (mapcat :imports namespaces-src)))]
    (vec (sort (set (filter identity (map #(included-lib % ns-libs ns->lib) used-namespaces)))))))
