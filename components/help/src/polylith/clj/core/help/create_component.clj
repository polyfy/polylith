(ns ^:no-doc polylith.clj.core.help.create-component
     (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a component.\n"
       "\n"
       "  poly create component name:" (s/key "NAME" cm) " [interface:" (s/key "INTERFACE" cm) "] [" (s/key ":git-add" cm) "]\n"
       "    " (s/key "NAME" cm) " = The name of the component to create.\n"
       "\n"
       "    " (s/key ":git-add" cm) " = If " (s/key ":vcs" cm) " > " (s/key ":auto-add" cm) " in workspace.edn is set to false,\n"
       "               then we can pass in this flag instead, to explicitly add the\n"
       "               created files to git.\n"
       "\n"
       "    " (s/key "INTERFACE" cm) " = The name of the interface (namespace) or " (s/key "NAME" cm) " if not given.\n"
       "\n"
       "  Example:\n"
       "    poly create c name:user\n"
       "    poly create component name:user\n"
       "    poly create component name:user :git-add\n"
       "    poly create component name:admin interface:user"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
