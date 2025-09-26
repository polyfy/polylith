(ns ^:no-doc polylith.clj.core.ws-file-reader.from-4-to-5.converter
  (:require [polylith.clj.core.common.interface :as common]))

(defn with-source-types [{:keys [namespaces] :as entity}]
  (assoc entity :source-types
         (common/source-types namespaces)))

(defn convert [{:keys [bases components projects] :as workspace}]
  (assoc workspace :ws-dialects #{"clj"}
                   :bases (mapv with-source-types bases)
                   :components (mapv with-source-types components)
                   :projects (mapv with-source-types projects)))
