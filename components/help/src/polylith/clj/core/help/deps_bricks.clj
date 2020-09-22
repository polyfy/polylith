(ns polylith.clj.core.help.deps-bricks
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows all brick dependencies.\n"
       "\n"
       "  poly deps\n"
       "\n"
       "         " (color/yellow cm "p      \n")
       "         " (color/yellow cm "a  u  u\n")
       "         " (color/yellow cm "y  s  t\n")
       "         " (color/yellow cm "e  e  i\n")
       "  brick  " (color/yellow cm "r  r  l\n")
       "  --------------\n"
       "  " (color/green cm "payer") "  ·  x  x\n"
       "  " (color/green cm "user") "   ·  ·  x\n"
       "  " (color/green cm "util") "   ·  ·  ·\n"
       "  " (color/blue cm "cli") "    x  ·  ·\n"
       "\n"
       "  In this example, " (color/green cm "payer") " uses " (color/yellow cm "user") " and " (color/yellow cm "util") ", " (color/green cm "user") " uses " (color/yellow cm "util") ",\n"
       "  and " (color/blue cm "cli") " uses " (color/yellow cm "payer") ". Each usage comes from at least one " (color/purple cm ":require") "\n"
       "  statement in the brick."))

(defn print-help [color-mode]
  (println (help color-mode)))
