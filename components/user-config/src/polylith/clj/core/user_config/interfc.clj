(ns polylith.clj.core.user-config.interfc
  (:require [polylith.clj.core.util.interfc.str :as str-util]))

(defn home-dir []
  (let [dir (System/getProperty "user.home")]
     (str-util/skip-if-ends-with dir "/")))

(defn config-content []
  (try
    (read-string (slurp (str (home-dir) "/.polylith/config.edn")))
    (catch Exception _
      {})))

(defn thousand-separator []
  (:thousand-separator (config-content) ","))

(defn color-mode []
  (:color-mode (config-content) "dark"))
