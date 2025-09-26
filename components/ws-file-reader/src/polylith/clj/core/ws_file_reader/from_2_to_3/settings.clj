(ns ^:no-doc polylith.clj.core.ws-file-reader.from-2-to-3.settings
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

(defn profile [[profile-name settings]]
  (assoc settings :name profile-name
                  :type "profile"))

(defn convert [{:keys [bases components projects settings] :as workspace}]
  (let [bricks-settings (:bricks settings)
        projects-settings (:projects settings)]
    (assoc workspace :bases (move-settings-key-to-entity bases :keep-lib-versions bricks-settings)
                     :components (move-settings-key-to-entity components :keep-lib-versions bricks-settings)
                     :projects (convert-projects projects projects-settings)
                     :profiles (mapv profile (:profile-to-settings settings))
                     :settings (dissoc settings :bases :projects :profile-to-settings))))
