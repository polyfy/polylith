(ns polylith.clj.core.help.create-component
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a component.\n"
       "\n"
       "  poly create c name:" (s/key "NAME" cm) " [interface:" (s/key "INTERFACE" cm) "]\n"
       "    " (s/key "NAME" cm) " = The name of the component to create.\n"
       "\n"
       "    " (s/key "INTERFACE" cm) " = The name of the interface (namespace) or " (s/key "NAME" cm) " if not given.\n"
       "\n"
       "  Example:\n"
       "    poly create c name:user\n"
       "    poly create c name:admin interface:user"
       "    poly create component name:user\n"))

(defn print-help [color-mode]
  (println (help-text color-mode)))
