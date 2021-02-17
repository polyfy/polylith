(ns polylith.clj.core.user-config.core
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn home-dir []
  (-> (System/getProperty "user.home")
      (str-util/skip-if-ends-with file/sep)))

(defn- config-content []
  (let [config-dir (str (home-dir) file/sep ".polylith" file/sep "config.edn")]
    (try
      (read-string (slurp config-dir))
      (catch Exception _
        {}))))

(defn color-mode []
  (:color-mode (config-content) "none"))

(defn empty-character []
  (:empty-character (config-content) "."))

(defn m2-dir []
  (:m2-dir (config-content)
           (str (home-dir) "/.m2")))

(defn thousand-sep []
  (:thousand-sep (config-content) ","))
