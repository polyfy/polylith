(ns polylith.clj.core.tap.core
  (:require [clojure.string :as str]
            [portal.api :as portal]))

(defn command [cmd]
  (if (str/blank? cmd)
    "open"
    cmd))

(defn execute [cmd]
  (case (command cmd)
    "open" (do
             (portal/open)
             (add-tap #'portal/submit))
    "clear" (portal/clear)
    "close" (portal/close)
    (println "Unknown portal command: " cmd ". Valid commands: (blank), clear, or close")))
