(ns polylith.clj.core.help.create-environment
  (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates an environment.\n"
       "\n"
       "  poly create e name:" (s/key "NAME" cm) "\n"
       "    " (s/key "NAME" cm) " = The name of the environment to create.\n"
       "\n"
       "  Example:\n"
       "    poly create e name:myenv"))

(defn print-help [color-mode]
  (println (help-text color-mode)))
