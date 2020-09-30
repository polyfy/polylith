(ns polylith.clj.core.help.deps
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.help.deps-env :as deps-env]
            [polylith.clj.core.help.deps-brick :as deps-brick]
            [polylith.clj.core.help.deps-bricks :as deps-bricks]
            [polylith.clj.core.help.deps-brick-env :as deps-brick-env]))

(defn help [cm]
  (str "  Shows dependencies.\n"
       "\n"
       "  poly deps [env:" (s/key "ENV" cm) "] [brick:" (s/key "BRICK" cm) "]\n"
       "    (omitted) = Show dependencies for all bricks.\n"
       "    " (s/key "ENV" cm) "       = Show dependencies for specified environment.\n"
       "    " (s/key "BRICK" cm) "     = Show dependencies for specified brick.\n"
       "\n"
       "  To get help for a specific diagram, type: \n"
       "    poly help deps " (s/key "ARGS" cm) ":\n"
       "      " (s/key "ARGS" cm) " = " (s/key ":env" cm) "         Help for the environment diagram.\n"
       "             " (s/key ":brick" cm) "       Help for the brick diagram.\n"
       "             " (s/key ":bricks" cm) "      Help for all bricks diagram.\n"
       "             " (s/key ":env :brick" cm) "  Help for the environment/brick diagram."
       "\n"
       "  Example:\n"
       "    poly deps\n"
       "    poly deps env:myenv\n"
       "    poly deps brick:mybrick\n"
       "    poly deps env:myenv brick:mybrick"))

(defn print-help [is-show-env show-brick? show-bricks? color-mode]
  (cond
    (and is-show-env show-brick?) (deps-brick-env/print-help color-mode)
    is-show-env (deps-env/print-help color-mode)
    show-brick? (deps-brick/print-help color-mode)
    show-bricks? (deps-bricks/print-help color-mode)
    :else (println (help color-mode))))
