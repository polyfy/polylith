(ns polylith.clj.core.help.deps-env
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn help [color-mode]
  (str "Shows dependencies for selected environment, e.g:\n"
       "         " (color/green color-mode "p      \n")
       "         " (color/green color-mode "a  u  u\n")
       "         " (color/green color-mode "y  s  t\n")
       "         " (color/green color-mode "e  e  i\n")
       "  brick  " (color/green color-mode "r  r  l\n")
       "  --------------\n"
       "  " (color/green color-mode "payer") "  ·  x  x\n"
       "  " (color/green color-mode "user") "   ·  ·  x\n"
       "  " (color/green color-mode "util") "   ·  ·  ·\n"
       "  " (color/blue color-mode "cli")  "    x  +  +\n"
       "\n"
       "  In this example, " (color/green color-mode "payer") " uses " (color/green color-mode "user") " and " (color/green color-mode "util") ", " (color/green color-mode "user") " uses " (color/green color-mode "util") ",\n"
       "  and " (color/blue color-mode "cli") " uses " (color/green color-mode "payer") ". Each usage comes from at least one " (color/purple color-mode ":require") "\n"
       "  statement in the brick. \n"
       "  When the environment is known, we also know which components are used.\n"))

(defn print-help [color-mode]
  (println (help color-mode)))
