(ns ^:no-doc polylith.clj.core.help.migrate)

(defn help []
  (str "  Migrates a workspace to the latest version.\n"
       "\n"
       "  poly migrate\n"
       "\n"
       "  As of version 0.2.19, bases, components, and projects store their configuration in config.edn files.\n"
       "  This command will move configuration from the :bricks and :projects keys in workspace.edn\n"
       "  to corresponding config.edn files."))

(defn print-help []
  (println (help)))

(comment
  (print-help)
  #__)
