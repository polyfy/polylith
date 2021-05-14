(ns polylith.clj.core.workspace-clj.config-from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.validator.interface :as validator]
            [polylith.clj.core.workspace-clj.leiningen.config-from-disk :as lein-config-from-disk]))

(defn read-and-validate-config-file [config-filename]
  (if (-> config-filename file/exists not)
    [false (str "Could not find config file: " config-filename)]
    (let [config (read-string (slurp config-filename))
          message (validator/validate-brick-config config)]
      (if message
        [false message]
        [true config]))))

(defn read-config-file [ws-type brick-dir]
  (let [[ok? data] (case ws-type
                    :leiningen1
                      (lein-config-from-disk/read-config-file brick-dir)
                    :toolsdeps1
                      [true {}]
                    :toolsdeps2
                      (let [config-filename (str brick-dir "/deps.edn")]
                        (read-and-validate-config-file config-filename)))]
    (if ok?
      data
      (throw (Exception. (str data))))))
