(ns polylith.clj.core.help.deps
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.help.deps-project :as deps-project]
            [polylith.clj.core.help.deps-brick :as deps-brick]
            [polylith.clj.core.help.deps-workspace :as deps-workspace]
            [polylith.clj.core.help.deps-brick-project :as deps-brick-project]))

(defn help [cm]
  (str "  Shows dependencies.\n"
       "\n"
       "  poly deps [project:" (s/key "PROJECT" cm) "] [brick:" (s/key "BRICK" cm) "]\n"
       "    (omitted) = Show workspace dependencies.\n"
       "    " (s/key "PROJECT" cm) "   = Show dependencies for specified project.\n"
       "    " (s/key "BRICK" cm) "     = Show dependencies for specified brick.\n"
       "\n"
       "  To get help for a specific diagram, type: \n"
       "    poly help deps " (s/key "ARGS" cm) ":\n"
       "      " (s/key "ARGS" cm) " = " (s/key ":brick" cm) "           Help for the brick diagram.\n"
       "             " (s/key ":project" cm) "         Help for the project diagram.\n"
       "             " (s/key ":workspace" cm) "       Help for the workspace diagram.\n"
       "             " (s/key ":project :brick" cm) "  Help for the project/brick diagram.\n"
       "  Example:\n"
       "    poly deps\n"
       "    poly deps project:myproject\n"
       "    poly deps brick:mybrick\n"
       "    poly deps project:myproject brick:mybrick"))

(defn print-help [is-show-project is-show-brick is-show-workspace color-mode]
  (cond
    (and is-show-project is-show-brick) (deps-brick-project/print-help color-mode)
    is-show-project (deps-project/print-help color-mode)
    is-show-brick (deps-brick/print-help color-mode)
    is-show-workspace (deps-workspace/print-help color-mode)
    :else (println (help color-mode))))

(comment
  (print-help false false false "dark")
  (print-help false false true "dark") ; workspace
  (print-help true false false "dark") ; project
  (print-help false true false "dark") ; brick
  #__)
