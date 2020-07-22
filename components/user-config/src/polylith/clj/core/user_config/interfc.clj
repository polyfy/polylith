(ns polylith.clj.core.user-config.interfc)

(defn config-content []
  (try
    (read-string (slurp (str (System/getProperty "user.home") "/.polylith/config.edn")))
    (catch Exception _
      {})))

(defn thousand-separator []
  (:thousand-separator (config-content) ","))
