(ns ^:no-doc polylith.clj.core.ws-file.from-2-to-3.converter
  (:require [polylith.clj.core.common.interface :as common]))

(defn move-settings-key-to-entity [entities key name->settings]
  (reduce-kv
    (fn [entities entity-name project-settings]
      (let [index (common/find-entity-index entity-name entities)
            value (key project-settings)]
        (cond-> entities
                (and index value) (assoc-in [index key] value))))
    entities name->settings))

(defn convert-projects [projects projects-settings]
  (-> projects
      (move-settings-key-to-entity :test projects-settings)
      (move-settings-key-to-entity :necessary projects-settings)
      (move-settings-key-to-entity :keep-lib-versions projects-settings)))

(defn convert [{:keys [bases components projects settings] :as workspace}]
  (let [bricks-settings (:bricks settings)
        projects-settings (:projects settings)]
    (assoc workspace :bases (move-settings-key-to-entity bases :keep-lib-versions bricks-settings)
                     :components (move-settings-key-to-entity components :keep-lib-versions bricks-settings)
                     :projects (convert-projects projects projects-settings)
                     :settings (dissoc settings :bases :projects))))
