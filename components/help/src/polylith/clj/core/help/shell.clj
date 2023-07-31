(ns ^:no-doc polylith.clj.core.help.shell
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Starts an interactive shell with the name of the selected workspace, e.g.:\n"
       "    myworkspace$>\n"
       "\n"
       "  poly [shell] [" (s/key ":tap" cm) "] [" (s/key ":all" cm) "]\n"
       "\n"
       "  If " (s/key ":tap" cm) " is passed in, a Portal window that outputs " (s/key "tap>" cm) " statements is opened.\n"
       "\n"
       "  From here we can execute any poly command, e.g.:\n"
       "    myworkspace$> info\n"
       "\n"
       "  We can also use the built in autocomplete, e.g.:\n"
       "    myworkspace$> i\n"
       "\n"
       "  ...and when pressing the <tab> key, the 'i' is completed to 'info'.\n"
       "\n"
       "  This works for both commands and arguments, and is context sensitive.\n"
       "  If we for example type:\n"
       "    myworkspace$> deps brick:mybrick project:\n"
       "\n"
       "  ...and press <tab>, it will only suggest projects that include 'mybrick'.\n"
       "\n"
       "  Parameters that start with a : can be selected by just typing their name,\n"
       "  e.g. 'l' will select ':loc'. To distinguish between 'project:PROJECT' and\n"
       "  ':project' we need to type ':p' to select ':project'.\n"
       "\n"
       "  If " (s/key ":all" cm) " is passed in, the autocomplete will suggest all available parameters,\n"
       "  including rarely used ones and those that are only useful for maintainers of\n"
       "  the poly tool itself.\n"
       "\n"
       "  From the shell we also have access to these commands:\n"
       "    switch-ws " (s/key "ARG" cm) "  Switches to selected workspace.\n"
       "    tap [" (s/key "ARG" cm) "]      Opens (or closes/cleans) a portal window that outputs " (s/key "tap>" cm) " statements.\n"
       "    exit           Exits the shell.\n"
       "    quit           Quits the shell.\n"
       "\n"
       "  It's also possible to start a shell and switch to a workspace at the same time, e.g.:\n"
       "    poly shell ws-dir:examples/doc-example\n"
       "    poly shell ws-file:realworld.edn\n"
       "\n"
       "  An alternative way of exiting the shell is by pressing <ctrl>+C or <ctrl>+D."))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
