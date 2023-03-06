(ns polylith.clj.core.workspace-clj.config-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-config-file [ws-type entity-name entity-dir entity-path validator]
  (let [config-filename (str entity-path "/deps.edn")]
    (-> (case ws-type
          :toolsdeps1
          {:config {:paths ["src" "resources"]
                    :aliases {:test {:extra-paths ["test"]}}}}
          :toolsdeps2
          (if (-> config-filename file/exists not)
            {:error (str "Could not find config file: " entity-dir "/deps.edn")}
            (let [config (config/read-deps-file config-filename)
                  message (validator config)]
              (if message
                {:error message}
                {:config config}))))
        (assoc :name entity-name))))

(defn filter-config-files [configs-and-errors]
  (let [configs (vec (sort-by :name (filter :config configs-and-errors)))
        errors (vec (sort-by :name (filter :error configs-and-errors)))]
    [configs errors]))

(defn read-brick-config-files [ws-dir ws-type entity-dir]
  (-> (map #(read-config-file ws-type %
                              (str entity-dir "/" %)
                              (str ws-dir "/" entity-dir "/" %)
                              validator/validate-brick-config)
           (file/directories (str ws-dir (str "/" entity-dir))))
      (filter-config-files)))

(defn read-project-deployable-config-files [ws-dir ws-type]
  (map #(assoc (read-config-file ws-type %
                                 (str "projects/" %)
                                 (str ws-dir "/projects/" %)
                                 (partial validator/validate-project-deployable-config ws-type))
               :is-dev false
               :project-name %
               :project-dir (str ws-dir "/projects/" %)
               :project-config-dir (str ws-dir "/projects/" %))
       (file/directories (str ws-dir (str "/projects")))))

(defn read-project-dev-config-file [ws-dir ws-type]
  (let [config-filename (str ws-dir "/deps.edn")
        project-dir (str ws-dir "/development")]
    (assoc (read-config-file ws-type
                             "development"
                             "development"
                             ws-dir #(validator/validate-project-dev-config ws-type %))
           :is-dev true
           :config-filename config-filename
           :project-dir project-dir
           :project-name "development")))

(defn read-project-config-files [ws-dir ws-type]
  (-> (into [] cat [[(read-project-dev-config-file ws-dir ws-type)]
                    (read-project-deployable-config-files ws-dir ws-type)])
      (filter-config-files)))
