(ns polylith.clj.core.user-config.core
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn home-dir []
  (let [dir (System/getProperty "user.home")]
    (str-util/skip-if-ends-with dir file/sep)))

(defn- config-content []
  (let [config-dir (str (home-dir) file/sep ".polylith" file/sep "config.edn")]
    (try
      (read-string (slurp config-dir))
      (catch Exception _
        (println "Can't read config file: " config-dir)
        {}))))

(defn thousand-separator []
  (:thousand-separator (config-content) ","))

(defn color-mode []
  (:color-mode (config-content) "none"))

(defn empty-character []
  (:empty-character (config-content) "."))
