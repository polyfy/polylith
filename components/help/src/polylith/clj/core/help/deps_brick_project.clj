(ns polylith.clj.core.help.deps-brick-project
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Shows dependencies for selected brick and project.\n"
       "\n"
       "  poly deps project:" (s/key "PROJECT" cm) " brick:" (s/key "BRICK" cm) "\n"
       "    " (s/key "PROJECT" cm) " = The project (name or alias) to show dependencies for.\n"
       "    " (s/key "BRICK" cm) "   = The brick to show dependencies for.\n"
       "\n"
       "  used by  <  " (color/green cm "user") "  >  uses\n"
       "  -------              ----\n"
       "  " (color/green cm "payer") "                " (color/green cm "util") "\n"
       "\n"
       "  In this example, " (color/green cm "user") " is used by " (color/green cm "payer") " and it uses " (color/green cm "util") " itself.\n"
       "\n"
       "  Example:\n"
       "    poly deps project:myproject brick:mybrick"))

(defn print-help [color-mode]
  (println (help color-mode)))
