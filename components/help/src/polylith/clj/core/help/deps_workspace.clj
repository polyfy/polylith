(ns ^:no-doc polylith.clj.core.help.deps-workspace
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows dependencies for the workspace.\n"
       "\n"
       "  poly deps [out:" (s/key "FILENAME" cm) "]\n"
       "    " (s/key "FILENAME" cm) " = The name of the text file to create, containing the\n"
       "               output from this command.\n"
       "         " (color/yellow cm "p      \n")
       "         " (color/yellow cm "a  u  u\n")
       "         " (color/yellow cm "y  s  t\n")
       "         " (color/yellow cm "e  e  i\n")
       "  brick  " (color/yellow cm "r  r  l\n")
       "  --------------\n"
       "  " (color/green cm "payer") "  .  x  t\n"
       "  " (color/green cm "user") "   .  .  x\n"
       "  " (color/green cm "util") "   .  .  .\n"
       "  " (color/blue cm "cli") "    x  .  .\n"
       "\n"
       "  In this example, " (color/green cm "payer") " uses " (color/yellow cm "user") " from the src context, and " (color/yellow cm "util") " from\n"
       "  the test context (indicated by 't'). " (color/green cm "user") " uses " (color/yellow cm "util") " and " (color/blue cm "cli") " uses " (color/yellow cm "payer") ".\n"
       "  Each usage comes from at least one " (color/purple cm ":require") " statement in the brick."))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
