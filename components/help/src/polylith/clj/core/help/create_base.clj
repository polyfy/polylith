(ns polylith.clj.core.help.create-base
  (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a base.\n"
       "\n"
       "  poly create b name:" (s/key "NAME" cm) "\n"
       "    " (s/key "NAME" cm) " = The name of the base to create.\n"
       "\n"
       "  Example:\n"
       "    poly create b name:mybase"
       "    poly create base name:mybase"))

(defn print-help [color-mode]
  (println (help-text color-mode)))
