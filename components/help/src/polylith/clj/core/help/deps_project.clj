(ns ^:no-doc polylith.clj.core.help.deps-project
     (:require [polylith.clj.core.help.shared :as s]
               [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows dependencies for selected project.\n"
       "\n"
       "  poly deps project:" (s/key "PROJECT" cm) " [out:" (s/key "FILENAME" cm) "]\n"
       "    " (s/key "PROJECT" cm) " = The project name or alias to show dependencies for.\n"
       "    " (s/key "FILENAME" cm) " = The name of the text file to create, containing the\n"
       "               output from this command.\n"
       "\n"
       "         " (color/green cm "p      \n")
       "         " (color/green cm "a  u  u\n")
       "         " (color/green cm "y  s  t\n")
       "         " (color/green cm "e  e  i\n")
       "  brick  " (color/green cm "r  r  l\n")
       "  --------------\n"
       "  " (color/green cm "payer") "  .  x  t\n"
       "  " (color/green cm "user") "   .  .  x\n"
       "  " (color/green cm "util") "   .  .  .\n"
       "  " (color/blue cm "cli") "    x  +  +\n"
       "\n"
       "  When the project is known, we also know which components are used.\n"
       "\n"
       "  In this example, " (color/green cm "payer") " uses " (color/green cm "user") " in the src context, and " (color/green cm "util") " only\n"
       "  in the test context. " (color/green cm "user") " uses " (color/green cm "util") "," " and " (color/blue cm "cli") " uses " (color/green cm "payer") ". The 't'\n"
       "  means that " (color/green cm "payer") " is only used in the test context by " (color/green cm "user") ". The +\n"
       "  signs mark indirect dependencies, while - signs (not present here)\n"
       "  mark indirect dependencies in the test context. Here the " (color/blue cm "cli") " base\n"
       "  depends on " (color/green cm "user") " and " (color/green cm "util") ", via 'cli > payer > user' and\n"
       "  'cli > payer > util'. Each usage comes from at least one " (color/purple cm ":require") "\n"
       "  statement in the brick.\n"
       "\n"
       "  Example:\n"
       "    poly deps project:myproject\n"
       "    poly deps project:myproject out:myproject-deps.txt"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
