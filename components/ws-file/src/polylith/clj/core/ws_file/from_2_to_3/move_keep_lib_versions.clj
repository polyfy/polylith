(ns ^:no-doc polylith.clj.core.ws-file.from-2-to-3.move-keep-lib-versions
  (:require [polylith.clj.core.common.interface :as common]))

(defn move-project-key [key entities name->settings]
  (reduce-kv (fn [entities entity-name project-settings]
               (let [index (common/find-entity-index entity-name entities)
                     value (key project-settings)]
                 (cond-> entities
                         (and index value) (assoc-in [index key] value))))
             entities name->settings))

(defn convert [{:keys [bases components projects settings] :as workspace}]
  (let [bricks-settings (:bricks settings)
        projects-settings (:projects settings)]
    (assoc workspace :bases (move-project-key :keep-lib-versions bases bricks-settings)
                     :components (move-project-key :keep-lib-versions components bricks-settings)
                     :projects (move-project-key :keep-lib-versions projects projects-settings))))
