(ns polylith.clj.core.user-config.core
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.util.interfc.str :as str-util]))

(defn home-dir []
  (let [dir (System/getProperty "user.home")]
    (str-util/skip-if-ends-with dir file/sep)))

(defn- config-content []
  (let [config-dir (str (home-dir) file/sep ".polylith" file/sep "config.edn")]
    (try
      (read-string (slurp config-dir))
      (catch Exception _
        (println "Couldn't read config file: " config-dir)
        {}))))

(defn thousand-separator []
  (:thousand-separator (config-content) ","))

(defn color-mode []
  (:color-mode (config-content) "dark"))
