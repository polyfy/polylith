(ns ^:no-doc polylith.clj.core.config-reader.config-reader
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.config-reader.deps-reader :as deps-reader]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-deps-edn-file [ws-type entity-name entity-type entity-dir entity-path validator]
  (let [config-filename (str entity-path "/deps.edn")
        short-config-filename (str entity-dir "/deps.edn")]
    (-> (case ws-type
          :toolsdeps1
          (let [{:keys [config]} (deps-reader/read-deps-file config-filename short-config-filename)]
            (if config
              {:deps config}
              (if (= "project" entity-type)
                {:deps {}}
                {:deps {:paths ["src" "resources"]
                               :aliases {:test {:extra-paths ["test"]}}}})))
          :toolsdeps2
          (let [{:keys [config error]} (deps-reader/read-deps-file config-filename short-config-filename)]
            (if error
              {:error error}
              (let [message (validator ws-type config short-config-filename)]
                (if message
                  {:error message}
                  {:deps config})))))
        (assoc :name entity-name
               :type entity-type))))

(defn filter-config-files [configs-and-errors]
  (let [configs (vec (sort-by :name (filter :deps configs-and-errors)))
        errors (vec (sort-by :name (filter :error configs-and-errors)))]
    [configs errors]))

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

(defn read-brick-config-files [ws-dir ws-type entity-type]
  (-> (map #(read-deps-edn-file ws-type % entity-type
                                (str entity-type "s/" %)
                                (str ws-dir "/" entity-type "s/" %)
                                validator/validate-brick-config)
           (dirs-with-deps-file ws-dir entity-type))
      (filter-config-files)))

(defn read-project-deployable-config-files [ws-dir ws-type]
  (map #(assoc (read-deps-edn-file ws-type % "project"
                                   (str "projects/" %)
                                   (str ws-dir "/projects/" %)
                                   validator/validate-project-deployable-config)
          :is-dev false
          :project-name %
          :project-dir (str ws-dir "/projects/" %)
          :project-config-dir (str ws-dir "/projects/" %))
       (dirs-with-deps-file ws-dir "project")))

(defn read-project-dev-config-file [ws-dir ws-type]
  (let [filename (str ws-dir "/deps.edn")]
    (let [{:keys [config error]} (deps-reader/read-deps-file filename "deps.edn")]
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
             :project-config-dir ws-dir}))))))

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

(defn read-workspace-config-file [ws-dir]
  (let [filename-path (str ws-dir "/workspace.edn")]
    (let [{:keys [config error]} (deps-reader/read-deps-file filename-path "workspace.edn")]
      (if error
        {:error error}
        (let [message (validator/validate-workspace-config config)]
          (if message
            {:error (str "Error in ./workspace.edn: " message)
             :config config}
            {:config config}))))))
