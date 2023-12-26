(ns ^:no-doc polylith.clj.core.ws-file.from-2-to-3.move-keep-lib-versions
  (:require [polylith.clj.core.common.interface :as common]))

(defn move-keep-lib-versions [entities name->settings]
  (reduce-kv (fn [entities entity-name {:keys [keep-lib-versions]}]
               (let [index (common/find-entity-index entity-name entities)]
                 (cond-> entities
                         (and index
                              keep-lib-versions) (assoc-in [index :keep-lib-versions]
                                                           keep-lib-versions))))
             entities name->settings))

(defn convert [{:keys [bases components projects settings] :as workspace}]
  (let [bricks-settings (:bricks settings)
        projects-settings (:projects settings)]
    (assoc workspace :bases (move-keep-lib-versions bases bricks-settings)
                     :components (move-keep-lib-versions components bricks-settings)
                     :projects (move-keep-lib-versions projects projects-settings))))
