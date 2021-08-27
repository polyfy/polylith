(ns polylith.clj.core.help.prompt)

(defn help []
  (str "  poly prompt\n"
       "\n"
       "  Starts a prompt with the name of the current workspace, e.g.:\n"
       "    myworkspace$>\n"
       "\n"
       "  From here we are free to execute any command we want, e.g.:\n"
       "    myworkspace$> info\n"
       "\n"
       "  The idea is to get get rid of the startup time of the command\n"
       "  and get instant feedback.\n"
       "\n"
       "  Exit the interactive mode by typing 'exit' or 'quit'."))

(defn print-help []
  (println (help)))

(comment
  (print-help)
  #__)
