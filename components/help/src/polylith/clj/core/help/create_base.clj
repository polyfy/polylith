(ns ^:no-doc polylith.clj.core.help.create-base
  (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Creates a base.\n"
       "\n"
       "  poly create base name:" (s/key "NAME" cm) " [" (s/key ":git-add" cm) "] \n"
       "    " (s/key "NAME" cm) " = The name of the base to create.\n"
       "\n"
       "    " (s/key ":git-add" cm) " = If " (s/key ":vcs" cm) " > " (s/key ":auto-add" cm) " in workspace.edn is set to false,\n"
       "               then we can pass in this flag instead to explicitly add the\n"
       "               created files to git.\n"
       "\n"
       "  Example:\n"
       "    poly create b name:mybase\n"
       "    poly create base name:mybase\n"
       "    poly create base name:mybase :git-add"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
