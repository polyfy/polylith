(ns ^:no-doc polylith.clj.core.config-reader.config-reader
  (:require [clojure.tools.deps :as tools-deps]
            [clojure.edn :as edn]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.config-reader.deps-reader :as deps-reader]
            [polylith.clj.core.config-reader.json-reader :as json-reader]
            [polylith.clj.core.validator.interface :as validator]))

(defn get-current-basis-config []
  (when-let [basis-path (System/getProperty "clojure.basis")]
    (when (file/exists basis-path)
      (let [basis (edn/read-string (slurp basis-path))
            ;; Only extract the -Sdeps configuration, not the resolved dependencies
            extra-config (get-in basis [:basis-config :extra])]
        extra-config))))

(defn read-deps-edn-config-file [ws-type entity-dir entity-path validator]
  (let [config-path (str entity-path "/deps.edn")
        short-config-path (str entity-dir "/deps.edn")]
    (when (file/exists config-path)
      (let [{:keys [error] deps-config :config} (deps-reader/read-deps-file config-path short-config-path)
            current-basis (get-current-basis-config)
            config (tools-deps/merge-edns [deps-config current-basis])
            message (validator ws-type config short-config-path)
            error (or error message)]
        (cond-> {}
                error (assoc :error error)
                config (assoc :deps config))))))

(defn read-package-json-config-file [path entity-dir validator]
  (let [config-path (str path "/package.json")
        short-config-path (str entity-dir "/package.json")]
    (when (file/exists config-path)
      (let [{:keys [config error]} (when (file/exists config-path)
                                     (json-reader/read-file config-path))
            message (validator config short-config-path)
            error (or error message)]
        (cond-> {}
                error (assoc :npm-error error)
                config (assoc :npm-config config))))))

(defn read-config-files
  "Read the deps.edn and package.json files (if exist)
   and keep them in the keys :deps and :package, or :error if any errors"
  [ws-type entity-name entity-type entity-dir entity-path deps-validator package-validator]
  (let [{:keys [deps error]} (read-deps-edn-config-file ws-type entity-dir entity-path deps-validator)
        {:keys [npm-config npm-error]} (read-package-json-config-file entity-path entity-dir package-validator)
        error (or error npm-error)]
    (cond-> {:name entity-name
             :type entity-type}
            deps (assoc :deps deps)
            npm-config (assoc :package npm-config)
            error (assoc :error error))))

(defn with-config [config]
  (and (not (:error config))
       (some config [:deps :package])))

(defn filter-config-files [configs-and-errors]
  (let [configs (vec (sort-by :name (filter with-config configs-and-errors)))
        errors (vec (sort-by :name (filter :error configs-and-errors)))]
    [configs errors]))

(defn dirs [ws-dir entity-type]
  (let [dir (str ws-dir "/" entity-type "s")]
    (->> dir
         (file/directories)
         (sort))))

(defn read-brick-config-files [ws-dir ws-type entity-type]
  (-> (map #(read-config-files ws-type % entity-type
                               (str entity-type "s/" %)
                               (str ws-dir "/" entity-type "s/" %)
                               validator/validate-brick-deps-config
                               validator/validate-brick-package-config)
           (dirs ws-dir entity-type))
      (filter-config-files)))

(defn default-brick-deps-config-file [entity-name entity-type]
  (-> {:deps {:paths ["src" "resources"]
              :aliases {:test {:extra-paths ["test"]}}}}
      (assoc :name entity-name
             :type entity-type)))

(defn read-or-use-default-brick-config-files [ws-dir ws-type entity-type]
  (condp = ws-type
    :toolsdeps1 [(mapv #(default-brick-deps-config-file % entity-type)
                       (dirs ws-dir entity-type)) []]
    :toolsdeps2 (read-brick-config-files ws-dir ws-type entity-type)))

(defn read-project-deployable-config-files [ws-dir ws-type]
  (map #(assoc (read-config-files ws-type % "project"
                                  (str "projects/" %)
                                  (str ws-dir "/projects/" %)
                                  validator/validate-deployable-project-deps-config
                                  validator/validate-deployable-project-package-config)
               :is-dev false
               :project-name %
               :project-dir (str ws-dir "/projects/" %)
               :project-config-dir (str ws-dir "/projects/" %))
       (dirs ws-dir "project")))

(defn read-development-config-files [ws-dir ws-type]
  (assoc (read-config-files ws-type "development" "project" "." ws-dir
                            validator/validate-project-dev-config
                            validator/validate-dev-project-package-config)
         :is-dev true
         :project-name "development"
         :project-dir (str ws-dir "/development")
         :project-config-dir ws-dir))

(defn clean-project-configs [configs]
  (mapv #(dissoc %
                 :is-dev
                 :project-name
                 :project-dir
                 :project-config-dir)
        configs))

(defn read-project-config-files [ws-dir ws-type]
  (-> (into [] cat [[(read-development-config-files ws-dir ws-type)]
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
