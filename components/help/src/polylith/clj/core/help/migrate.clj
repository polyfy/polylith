(ns polylith.clj.core.help.migrate)

(defn print-help []
  (str "  Migrates a workspace to the latest version.\n"
       "\n"
       "  If the workspace hasn't been migrated already, then this command will create a new\n"
       "  ./workspace.edn file + a deps.edn file per brick. The :polylith key from the root\n"
       "  deps.edn file will be removed and all project deps.edn files will be updated.\n"
       "\n"
       "  The migration tool will use the :ns-to-lib key to figure out what libraries are\n"
       "  used in each brick. It's recommended to go through all bricks and make sure that all\n"
       "  external libraries are specified in each brick's deps.edn file. Also remove the \n"
       "  corresponding paths from the project's deps.edn files (which will instead be implicitly\n"
       "  included via the :local/root definitions). The paths in ./deps.edn is left untouched\n"
       "  and the reason is that the :local/root syntax is not supported by all IDE's\n"
       "\n"
       "  Starting from version 0.2.0-alpha10, the tool supports specifying dependencies per\n"
       "  brick in its own deps.edn files. Workspace specific config is stored in ./workspace.edn\n"
       "  instead of the :polylith key in ./deps which was the case prior to to this version.\n"))

(comment
  (print-help)
  #__)
