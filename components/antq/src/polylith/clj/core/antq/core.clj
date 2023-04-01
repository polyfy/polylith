(ns polylith.clj.core.antq.core
  (:require [antq.api :as antq]
            [polylith.clj.core.common.interface :as common]))

(defn truncate [version type]
  (if (= :git-sha type)
    (subs version 0 7)
    version))

(defn key-value [{:keys [name version latest-version type]}]
  [[name (truncate version type)]
   (truncate latest-version type)])

(defn library->latest-version
  "Returns a map where the key is [lib-name lib-version]
   and the value is the latest version of the library."
  [{:keys [projects]}]
  (let [dev-config (:config (common/find-project "development" projects))]
    (into {} (map key-value
                  (antq/outdated-deps dev-config)))))
