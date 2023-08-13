(ns ^:no-doc polylith.clj.core.help.migrate)

(defn help []
  (str "  Migrates a workspace to the latest version.\n"
       "\n"
       "  poly migrate\n"
       "\n"
       "  If the workspace hasn't been migrated already, then this command will create a new\n"
       "  ./workspace.edn file + a deps.edn file per brick. All project deps.edn files will be\n"
       "  updated. The libraries in each project's deps.edn file will be sorted, so it can be\n"
       "  an idea to manually change that order and put bricks first followed by the\n"
       "  libraries.\n"
       "\n"
       "  The migration tool will use the :ns-to-lib key to figure out what libraries are\n"
       "  used in each brick. After the migration, it's recommended to go through all the\n"
       "  bricks and make sure that all libraries it uses are also specified in each brick's\n"
       "  deps.edn file.\n"
       "\n"
       "  Continue by updating each project's deps.edn file and remove libraries that are\n"
       "  already indirectly included by bricks (via :local/root).\n"
       "  The paths in ./deps.edn is left untouched and the reason is that the :local/root\n"
       "  syntax is not supported by all IDE's.\n"
       "\n"
       "  Starting from version 0.2.0-alpha10, the tool supports specifying dependencies per\n"
       "  brick in its own deps.edn files. Workspace specific config is stored in\n"
       "  ./workspace.edn instead of the :polylith key in ./deps which was the case prior to\n"
       "  this version."))

(defn print-help []
  (println (help)))

(comment
  (print-help)
  #__)
