(ns polylith.clj.core.help.test
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.util.interface.color :as color]))

(defn help-text [color-mode]
  (str "  Executes brick and/or project tests.\n"
       "\n"
       "  poly test [" (s/key "ARGS" color-mode) "]\n"
       "\n"
       "  The brick tests are executed from all projects they belong to except for the development\n"
       "  project (if not " (s/key ":dev" color-mode) " is passed in):\n"
       "\n"

       "  ARGS              Tests to execute\n"
       "  ----------------  -------------------------------------------------------------\n"
       "  (empty)           All brick tests that are directly or indirectly changed.\n"
       "\n"
       "  " (s/key ":project" color-mode) "          All brick tests that are directly or indirectly changed +\n"
       "                    tests for changed projects.\n"
       "\n"
       "  " (s/key ":all-bricks" color-mode) "       All brick tests.\n"
       "\n"
       "  " (s/key ":all" color-mode) "              All brick tests + all project tests (except development).\n"
       "\n"
       "\n"
       "  To execute the brick tests from the development project, also pass in :dev:\n"
       "\n"
       "  ARGS              Tests to execute\n"
       "  ----------------  -------------------------------------------------------------\n"
       "  " (s/key ":dev" color-mode) "              All brick tests that are directly or indirectly changed,\n"
       "                    only executed from the development project.\n"
       "\n"
       "  " (s/key ":project :dev" color-mode) "     All brick tests that are directly or indirectly changed,\n"
       "                    executed from all projects (development included) +\n"
       "                    tests for changed projects (development included).\n"
       "\n"
       "  " (s/key ":all-bricks :dev" color-mode) "  All brick tests, executed from all projects\n"
       "                    (development included).\n"
       "\n"
       "  " (s/key ":all :dev" color-mode) "         All brick tests, executed from all projects\n"
       "                    (development included) + all project tests\n"
       "                    (development included).\n"
       "\n"
       "  Projects can also be explicitly selected with e.g. " (s/key "project:proj1" color-mode) " or\n"
       "  " (s/key "project:proj1:proj2" color-mode) ". Specifying " (s/key ":dev" color-mode) " is a shortcut for " (s/key "project:dev" color-mode) ".\n"
       "\n"
       "  We can also specify which bricks to include, by listing them like this:\n"
       "  brick:" (color/component "mycomponent" color-mode) ":" (color/component "another-component" color-mode) ":" (color/base "mybase" color-mode) "\n"
       "\n"
       "  Example:\n"
       "    poly test\n"
       "    poly test :project\n"
       "    poly test :all-bricks\n"
       "    poly test :all\n"
       "    poly test project:proj1\n"
       "    poly test project:proj1:proj2\n"
       "    poly test brick:mycomponent\n"
       "    poly test brick:mycomponent:mybase\n"
       "    poly test :dev\n"
       "    poly test :project :dev\n"
       "    poly test :all-bricks :dev\n"
       "    poly test :all :dev"))

(defn print-help [color-mode]
  (println (help-text color-mode)))

(comment
  (print-help "dark")
  #__)
