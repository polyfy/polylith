(ns polylith.clj.core.help.deps-env
     (:require [polylith.clj.core.help.shared :as s]
               [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows dependencies for selected environment.\n"
       "\n"
       "  poly deps env:" (s/key "ENV" cm) "\n"
       "    " (s/key "ENV" cm) " = The environment name or alias to show depenencies for.\n"
       "\n"
       "         " (color/green cm "p      \n")
       "         " (color/green cm "a  u  u\n")
       "         " (color/green cm "y  s  t\n")
       "         " (color/green cm "e  e  i\n")
       "  brick  " (color/green cm "r  r  l\n")
       "  --------------\n"
       "  " (color/green cm "payer") "  ·  x  x\n"
       "  " (color/green cm "user") "   ·  ·  x\n"
       "  " (color/green cm "util") "   ·  ·  ·\n"
       "  " (color/blue cm "cli") "    x  +  +\n"
       "\n"
       "  When the environment is known, we also know which components are used.\n"
       "\n"
       "  In this example, " (color/green cm "payer") " uses " (color/green cm "user") " and " (color/green cm "util") ", " (color/green cm "user") " uses " (color/green cm "util") "," " and " (color/blue cm "cli") " uses " (color/green cm "payer") ".\n"
       "  The + signs mark indirect depencencies. Here the " (color/blue cm "cli") " base depends on " (color/green cm "user") " and\n"
       "  " (color/green cm "util") ", via 'cli > payer > user' and 'cli > payer > util'. Each usage comes from\n"
       "  at least one " (color/purple cm ":require") " statement in the brick. \n"
       "\n"
       "  Example:\n"
       "    poly deps env:myenv"))

(defn print-help [color-mode]
  (println (help color-mode)))
