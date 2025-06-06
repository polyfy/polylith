(ns ^:no-doc polylith.clj.core.config-reader.config-reader
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.config-reader.deps-reader :as deps-reader]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-deps-edn-config-file [ws-type entity-name entity-type entity-dir entity-path validator]
  (let [deps-filename (str entity-path "/deps.edn")
        short-deps-filename (str entity-dir "/deps.edn")]
    (-> (let [{:keys [config error]} (deps-reader/read-deps-file deps-filename short-deps-filename)]
          (if error
            {:error error}
            (let [message (validator ws-type config short-deps-filename)]
              (if message
                {:error message}
                {:deps config}))))
        (assoc :name entity-name
               :type entity-type))))

(defn filter-config-files [configs-and-errors]
  (let [configs (vec (sort-by :name (filter :deps configs-and-errors)))
        errors (vec (sort-by :name (filter :error configs-and-errors)))]
    [configs errors]))

(defn dirs [ws-dir entity-type]
  (let [dir (str ws-dir "/" entity-type "s")]
    (->> dir
         (file/directories)
         (sort))))

(defn dirs-with-deps-file
  "If the deps.edn file doesn't exist, then it's probably because we have
   switched between branches in git and left a brick directory empty,
   which is the reason we skip these bricks/projects."
  [ws-dir entity-type]
  (let [dir (str ws-dir "/" entity-type "s")]
    (->> dir
         (file/directories)
         (filter #(file/exists (str dir "/" % "/deps.edn")))
         (sort))))

(defn read-brick-deps-edn-file [ws-dir ws-type entity-type]
  (-> (map #(read-deps-edn-config-file ws-type % entity-type
                                       (str entity-type "s/" %)
                                       (str ws-dir "/" entity-type "s/" %)
                                       validator/validate-brick-config)
           (dirs-with-deps-file ws-dir entity-type))
      (filter-config-files)))

(defn default-brick-deps-config-file [entity-name entity-type]
  (-> {:deps {:paths ["src" "resources"]
              :aliases {:test {:extra-paths ["test"]}}}}
      (assoc :name entity-name
             :type entity-type)))

(defn read-or-use-default-brick-deps-edn-files [ws-dir ws-type entity-type]
  (condp = ws-type
    :toolsdeps1 [(mapv #(default-brick-deps-config-file % entity-type)
                       (dirs ws-dir entity-type)) []]
    :toolsdeps2 (read-brick-deps-edn-file ws-dir ws-type entity-type)))

(defn read-project-deployable-config-files [ws-dir ws-type]
  (map #(assoc (read-deps-edn-config-file ws-type % "project"
                                          (str "projects/" %)
                                          (str ws-dir "/projects/" %)
                                          validator/validate-project-deployable-deps-config)
          :is-dev false
          :project-name %
          :project-dir (str ws-dir "/projects/" %)
          :project-config-dir (str ws-dir "/projects/" %))
       (dirs-with-deps-file ws-dir "project")))

(defn read-development-deps-config-file [ws-dir ws-type]
  (let [deps-filename (str ws-dir "/deps.edn")
        {:keys [config error]} (deps-reader/read-deps-file deps-filename "deps.edn")]
    (if error
      {:error error}
      (let [message (validator/validate-project-dev-config ws-type config "./deps.edn")]
        (if message
          {:error message}
          {:deps config
           :is-dev true
           :name "development"
           :type "project"
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
  (-> (into [] cat [[(read-development-deps-config-file ws-dir ws-type)]
                    (read-project-deployable-config-files ws-dir ws-type)])
      (filter-config-files)))

(defn read-workspace-config-file [ws-dir]
  (let [filename-path (str ws-dir "/workspace.edn")
        {:keys [config error]} (deps-reader/read-deps-file filename-path "workspace.edn")]
    (if error
      {:error error}
      (let [message (validator/validate-workspace-config config)]
        (if message
          {:error (str "Error in ./workspace.edn: " message)
           :config config}
          {:config config})))))
