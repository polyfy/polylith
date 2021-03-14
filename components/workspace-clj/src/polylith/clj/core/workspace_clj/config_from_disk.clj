(ns polylith.clj.core.workspace-clj.config-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.validator.interface :as validator]))

(defn read-and-validate-config-file [config-file-name]
  (if (-> config-file-name file/exists not)
    [false (str "Could not find config file: " config-file-name)]
    (let [config (read-string (slurp config-file-name))
          message (validator/validate-brick-config config)]
      (if message
        [false message]
        [true config]))))

(defn read-config-file [ws-type brick-dir]
  (when (= :toolsdeps2 ws-type)
    (let [config-file-name (str brick-dir "/deps.edn")
          [ok? data] (read-and-validate-config-file config-file-name)]
      (if ok?
        data
        (throw (Exception. (str data)))))))
