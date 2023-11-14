(ns ^:no-doc polylith.clj.core.antq.outdated
  (:require [antq.api :as antq]))

(defn truncate [version type]
  (if (= :git-sha type)
    (subs version 0 7)
    version))

(defn key-value [{:keys [name version latest-version type]}]
  [[name (truncate version type)]
   (truncate latest-version type)])

(defn entity-configs [configs]
  (let [{:keys [bases components projects]} configs]
    (concat bases components projects)))

(defn library->latest-version
  "Returns a map where the key is [lib-name lib-version]
   and the value is the latest version of the library."
  [configs]
  (into {} (map key-value)
        (antq/outdated-deps
          {:deps (into {} (set (mapcat #(map identity (-> % :deps :deps))
                                       (entity-configs configs))))})))
