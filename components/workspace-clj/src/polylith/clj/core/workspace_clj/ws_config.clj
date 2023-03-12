(ns polylith.clj.core.workspace-clj.ws-config
  (:require [polylith.clj.core.config-reader.interface :as config-reader]))

(defn ws-config-from-disk [ws-dir]
  (let [{:keys [config error]} (config-reader/read-workspace-config-file ws-dir)]
    (if error
      [config error]
      [config])))

(defn with-alias [[project alias]]
  [project {:alias alias}])

(defn ws-config-from-dev [{:keys [project-to-alias] :as config}]
  (let [projects (into {} (map with-alias project-to-alias))]
    [(-> config
         (dissoc :project-to-alias)
         (assoc :projects projects))]))
