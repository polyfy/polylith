(ns polylith.clj.core.workspace-clj.config-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface.config :as config]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-and-validate-config-file [config-filename]
  (if (-> config-filename file/exists not)
    [false (str "Could not find config file: " config-filename)]
    (let [config (config/read-deps-file config-filename)
          message (validator/validate-brick-config config)]
      (if message
        [false message]
        [true config]))))

(defn read-config-file [ws-type brick-dir]
  (let [[ok? data] (case ws-type
                     :toolsdeps1
                     [true {:paths ["src" "resources"]
                            :aliases {:test {:extra-paths ["test"]}}}]
                     :toolsdeps2
                     (let [config-filename (str brick-dir "/deps.edn")]
                       (read-and-validate-config-file config-filename)))]
    (if ok?
      data
      (throw (Exception. (str data))))))
