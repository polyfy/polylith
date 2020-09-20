(ns polylith.clj.core.help.deps
  (:require [polylith.clj.core.help.deps-env :as deps-env]
            [polylith.clj.core.help.deps-brick :as deps-brick]
            [polylith.clj.core.help.deps-bricks :as deps-bricks]
            [polylith.clj.core.help.deps-brick-env :as deps-brick-env]))

(defn help []
  (str "  Shows dependencies.\n"
       "\n"
       "  poly deps [env:ENV] [brick:BRICK]\n"
       "    (omitted) = Show dependencies for all bricks.\n"
       "    ENV       = Show dependencies for specified environment.\n"
       "    BRICK     = Show dependencies for specified brick.\n"
       "\n"
       "  To get help for a specific diagram, type: \n"
       "    poly help deps ARGS:\n"
       "      ARGS = :env        -> Help for environment diagram.\n"
       "             :brick      -> Help for brick diagram.\n"
       "             :bricks     -> Help for all bricks diagram.\n"
       "             :env :brick -> Help for environment+brick diagram."))

(defn print-help [show-env? show-brick? show-bricks? color-mode]
  (cond
    (and show-env? show-brick?) (deps-brick-env/print-help color-mode)
    show-env? (deps-env/print-help color-mode)
    show-brick? (deps-brick/print-help color-mode)
    show-bricks? (deps-bricks/print-help color-mode)
    :else (println (help))))
