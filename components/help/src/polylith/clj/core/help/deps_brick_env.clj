(ns polylith.clj.core.help.deps-brick-env
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Shows dependencies for selected brick and environment.\n"
       "\n"
       "  poly deps env:" (s/key "ENV" cm) " brick:" (s/key "BRICK" cm) "\n"
       "    " (s/key "ENV" cm) "   = The environment (name or alias) to show dependencies for.\n"
       "    " (s/key "BRICK" cm) " = The brick to show dependencies for.\n"
       "\n"
       "  used by  <  " (color/green cm "user") "  >  uses\n"
       "  -------              ----\n"
       "  " (color/green cm "payer") "                " (color/green cm "util") "\n"
       "\n"
       "  In this example, " (color/green cm "user") " is used by " (color/green cm "payer") " and it uses " (color/green cm "util") " itself.\n"
       "\n"
       "  Example:\n"
       "    poly deps env:myenv brick:mybrick"))

(defn print-help [color-mode]
  (println (help color-mode)))
