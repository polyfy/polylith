(ns polylith.clj.core.workspace-clj.config-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-config-file [ws-type entity-name entity-dir validator]
  (let [config-filename (str entity-dir "/deps.edn")
        output (case ws-type
                 :toolsdeps1
                 {:config {:paths ["src" "resources"]
                           :aliases {:test {:extra-paths ["test"]}}}}
                 :toolsdeps2
                 (if (-> config-filename file/exists not)
                   {:error (str "Could not find config file: " config-filename)}
                   (let [config (config/read-deps-file config-filename)
                         message (validator config)]
                     (if message
                       {:error message}
                       {:config config}))))]
    (assoc output :name entity-name)))

(defn read-config-files [ws-dir ws-type entity-dir validator]
  (let [configs-and-errors (map #(read-config-file ws-type % (str ws-dir "/" entity-dir "/" %) validator)
                                (file/directories (str ws-dir (str "/" entity-dir))))
        configs (vec (sort-by :name (filter :config configs-and-errors)))
        errors (vec (sort-by :name (filter :error configs-and-errors)))]
    [configs errors]))


(comment
  (read-config-files "examples/doc-example" :toolsdeps2 "bases" validator/validate-brick-config)
  (read-config-files "examples/doc-example" :toolsdeps2 "components" validator/validate-brick-config)
  (read-config-files "examples/doc-example" :toolsdeps2 "projects" #(validator/validate-project-deployable-config :toolsdeps2 %))
  #__)
