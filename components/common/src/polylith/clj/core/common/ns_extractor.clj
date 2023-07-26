(ns ^:no-doc polylith.clj.core.common.ns-extractor
  (:require [clojure.string :as str]))

(defn entity-imports [{:keys [namespaces]} sources]
  (mapcat #(mapcat :imports (% namespaces))
          sources))

(defn entity-namespaces [{:keys [namespaces]} sources]
  (mapcat #(map :namespace (% namespaces))
          sources))

(defn entities-namespaces [entities sources]
  (mapcat #(entity-namespaces % sources)
          entities))

(defn extract [suffixed-top-ns ns-to-extract]
  (when (str/starts-with? ns-to-extract suffixed-top-ns)
    (let [import (subs ns-to-extract (count suffixed-top-ns))
          idx (str/index-of import ".")]
      (when idx
        (let [root-ns (subs import 0 idx)
              depends-on-ns (subs import (inc idx))]
          {:root-ns       root-ns
           :depends-on-ns depends-on-ns})))))
