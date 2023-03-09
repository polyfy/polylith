(ns polylith.clj.core.workspace-clj.configs-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-config-file [ws-type entity-name brick? entity-dir entity-path validator]
  (let [config-filename (str entity-path "/deps.edn")
        short-config-filename (str entity-dir "/deps.edn")]
    (-> (case ws-type
          :toolsdeps1
          (if (-> config-filename file/exists)
            {:config (config/read-deps-file config-filename)}
            (if brick?
              {:config {:paths ["src" "resources"]
                        :aliases {:test {:extra-paths ["test"]}}}}
              {:config {}}))
          :toolsdeps2
          (if (-> config-filename file/exists not)
            {:error (str "Could not find config file: " short-config-filename)}
            (let [config (config/read-deps-file config-filename)
                  message (validator ws-type config short-config-filename)]
              (if message
                {:error message}
                {:config config}))))
        (assoc :name entity-name))))

(defn filter-config-files [configs-and-errors]
  (let [configs (vec (sort-by :name (filter :config configs-and-errors)))
        errors (vec (sort-by :name (filter :error configs-and-errors)))]
    [configs errors]))

(defn read-brick-config-files [ws-dir ws-type entity-dir]
  (-> (map #(read-config-file ws-type % true
                              (str entity-dir "/" %)
                              (str ws-dir "/" entity-dir "/" %)
                              validator/validate-brick-config)
           (file/directories (str ws-dir "/" entity-dir)))
      (filter-config-files)))

(defn read-project-deployable-config-files [ws-dir ws-type]
  (map #(assoc (read-config-file ws-type % false
                                 (str "projects/" %)
                                 (str ws-dir "/projects/" %)
                                 validator/validate-project-deployable-config)
               :is-dev false
               :project-name %
               :project-dir (str ws-dir "/projects/" %)
               :project-config-dir (str ws-dir "/projects/" %))
       (file/directories (str ws-dir "/projects"))))

(defn read-project-dev-config-file [ws-dir ws-type]
  (let [filename (str ws-dir "/deps.edn")]
    (if (-> filename file/exists not)
      {:error (str "Could not find config file: " filename)}
      (let [config (config/read-deps-file filename)
            message (validator/validate-project-dev-config ws-type config "./deps.edn")]
        (if message
          {:error message}
          {:config config
           :is-dev true
           :name "development"
           :project-name "development"
           :project-dir (str ws-dir "/development")
           :project-config-dir ws-dir})))))

(defn clean-project-configs [configs]
  (mapv #(dissoc %
                 :is-dev
                 :project-name
                 :project-dir
                 :project-config-dir)
        configs))

(defn read-project-config-files [ws-dir ws-type]
  (-> (into [] cat [[(read-project-dev-config-file ws-dir ws-type)]
                    (read-project-deployable-config-files ws-dir ws-type)])
      (filter-config-files)))
