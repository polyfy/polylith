(ns polylith.clj.core.user-config.interfc)

(defn config-content []
  (read-string (slurp (str (System/getProperty "user.home") "/.polylith/config.edn"))))

(defn thousand-separator []
  (:thousand-separator (config-content) ","))
