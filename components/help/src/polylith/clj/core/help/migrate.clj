(ns ^:no-doc polylith.clj.core.help.migrate
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Migrates a workspace to the latest version.\n"
       "\n"
       "  poly migrate [config-filename:" (s/key "FILENAME" cm) "]\n"
       "\n"
       "    "   (s/key "FILENAME" cm) " = The name of the file that stores configuration for projects and bricks.\n"
       "               If not given, config.edn will be used.\n"
       "\n"
       "  As of version 0.2.19, bases, components, and projects store their configuration in config.edn files.\n"
       "  This command will move configuration from the :bricks and :projects keys in workspace.edn\n"
       "  to corresponding config.edn files.\n"
       "\n"
       "  Example:\n"
       "    poly migrate\n"
       "    poly migrate config-filename:entity.edn\n"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
