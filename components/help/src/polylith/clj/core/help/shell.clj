(ns ^:no-doc polylith.clj.core.help.shell
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Starts an interactive shell with the name of the selected workspace, e.g.:\n"
       "    myworkspace$>\n"
       "\n"
       "  poly [shell] [" (s/key ":all" cm) "] [" (s/key ":tap" cm) "]\n"
       "\n"
       "    " (s/key ":all" cm) " = The autocomplete will suggest all available parameters,\n"
       "           including rarely used ones.\n"
       "\n"
       "    " (s/key ":tap" cm) " = A Portal window that outputs " (s/key "tap>" cm) " statements is opened.\n"
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
       "  From the shell we also have access to these commands:\n"
       "    switch-ws " (s/key "ARG" cm) "  Switches to selected workspace.\n"
       "    tap [" (s/key "ARG" cm) "]      Opens (or closes/cleans) a portal window that outputs " (s/key "tap>" cm) "\n"
       "                   statements.\n"
       "    exit           Exits the shell.\n"
       "    quit           Quits the shell.\n"
       "\n"
       "  It's also possible to start a shell and switch to a workspace at the same time,\n"
       "  e.g.:\n"
       "    poly shell ws-dir:examples/doc-example\n"
       "    poly shell ws-file:realworld.edn\n"
       "\n"
       "  An alternative way of exiting the shell is by pressing <ctrl>+C or <ctrl>+D.\n"
       "\n"
       "  Example:\n"
       "    poly shell\n"
       "    poly shell :all\n"
       "    poly shell :tap\n"
       "    poly shell :all :tap\n"
       "    poly doc page:shell"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")

  ; .......10|.......20|.......30|.......40|.......50|.......60|.......70|.......80|
  #__)
