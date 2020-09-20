(ns polylith.clj.core.help.deps-brick
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn help [color-mode]
  (str "  Shows dependencies for selected brick, e.g.:\n"
       "\n"
       "  used by  <  " (color/green color-mode "user") "  >  uses\n"
       "  -------              ----\n"
       "  " (color/green color-mode "payer") "                " (color/yellow color-mode "util") "\n"
       "\n"
       "  In this example, " (color/green color-mode "user") " is used by " (color/green color-mode "payer") " and it uses " (color/yellow color-mode "util") " itself."))

(defn print-help [color-mode]
  (println (help color-mode)))
