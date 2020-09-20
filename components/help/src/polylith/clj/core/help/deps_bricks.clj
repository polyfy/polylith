(ns polylith.clj.core.help.deps-bricks
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn help [color-mode]
  (str "Shows all brick dependencies, e.g:\n"
       "         " (color/yellow color-mode "p      \n")
       "         " (color/yellow color-mode "a  u  u\n")
       "         " (color/yellow color-mode "y  s  t\n")
       "         " (color/yellow color-mode "e  e  i\n")
       "  brick  " (color/yellow color-mode "r  r  l\n")
       "  --------------\n"
       "  " (color/green color-mode "payer") "  ·  x  x\n"
       "  " (color/green color-mode "user") "   ·  ·  x\n"
       "  " (color/green color-mode "util") "   ·  ·  ·\n"
       "  " (color/blue color-mode "cli") "    x  ·  ·\n"
       "\n"
       "  In this example, " (color/green color-mode "payer") " uses " (color/yellow color-mode "user") " and " (color/yellow color-mode "util") ", " (color/green color-mode "user") " uses " (color/yellow color-mode "util") ",\n"
       "  and " (color/blue color-mode "cli") " uses " (color/yellow color-mode "payer") ". Each usage comes from at least one " (color/purple color-mode ":require") "\n"
       "  statement in the brick.\n"))

(defn print-help [color-mode]
  (println (help color-mode)))
