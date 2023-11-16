(ns polylith.clj.core.lib.outdated
  (:require [polylith.clj.core.antq.ifc :as antq]))

;; todo: filter out ignored libraries.
(defn outdated-libraries [{:keys [configs]}]
  (set (map #(-> % ffirst)
            (antq/library->latest-version configs))))

(defn source-with-lib [[k v] outdated-libs lib->latest-version]
  (if (contains? outdated-libs k)
    [k (assoc v :latest-version
                (lib->latest-version [k (:version v)]))]
    [k v]))

(defn source-with-libs [deps outdated-libs lib->latest-version]
  (into {} (map #(source-with-lib % outdated-libs lib->latest-version)
                deps)))

(defn entity-with-libs [{:keys [lib-deps] :as entity} outdated-libs lib->latest-version]
  (let [{:keys [src test]} lib-deps]
    (cond-> entity
            src (assoc :src (source-with-libs src outdated-libs lib->latest-version))
            test (assoc :test (source-with-libs test outdated-libs lib->latest-version)))))

(defn entities-with-libs [entities outdated-libs lib->latest-version]
  (mapv #(entity-with-libs % outdated-libs lib->latest-version)
        entities))

(defn with-latest-libs [{:keys [bases components projects configs] :as workspace}]
  (let [library->latest-version (antq/library->latest-version configs)
        outdated-libs (outdated-libraries workspace)]
    (assoc workspace :bases (entities-with-libs bases outdated-libs library->latest-version)
                     :components (entities-with-libs components outdated-libs library->latest-version)
                     :projects (entities-with-libs projects outdated-libs library->latest-version))))
