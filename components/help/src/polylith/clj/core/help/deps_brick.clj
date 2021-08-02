(ns polylith.clj.core.help.deps-brick
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows dependencies for selected brick.\n"
       "\n"
       "  poly deps brick:" (s/key "BRICK" cm) "\n"
       "    " (s/key "BRICK" cm) " = The name of the brick to show dependencies for.\n"
       "\n"
       "  used by  <  " (color/green cm "user") "  >  uses\n"
       "  -------              ----\n"
       "  " (color/green cm "payer") "                " (color/yellow cm "util") "\n"
       "\n"
       "  In this example, " (color/green cm "user") " is used by " (color/green cm "payer") " and it uses " (color/yellow cm "util") " itself.\n"
       "  If a brick or interface ends with '(t)' then it indicatest that\n"
       "  it's only used from the test context.\n"
       "\n"
       "  Example:\n"
       "    poly deps brick:mybrick"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
