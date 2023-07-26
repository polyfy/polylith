(ns ^:no-doc polylith.clj.core.user-config.core
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn home-dir []
  (-> (System/getProperty "user.home")
      (str-util/skip-if-ends-with file/sep)))

(defn config-file-path []
  (let [xdg-config-home (some-> (not-empty (System/getenv "XDG_CONFIG_HOME"))
                                (str-util/skip-if-ends-with file/sep))]
    (str (or xdg-config-home
             (str (-> (System/getenv "HOME")
                      (str-util/skip-if-ends-with file/sep))
                  file/sep ".config"))
         file/sep "polylith"
         file/sep "config.edn")))

(defn config-content []
  (let [config-dir (config-file-path)]
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

(defn m2-home-dir? [m2-dir]
  (= m2-dir (str (home-dir) "/.m2")))

(defn thousand-separator []
  (:thousand-separator (config-content) ","))

(defn legacy-config-file-path []
  (str (home-dir) file/sep ".polylith" file/sep "config.edn"))
