(ns polylith.clj.core.help.test
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.util.interface.color :as color]))

(defn help-text [cm]
  (str "  Executes brick and/or project tests.\n"
       "\n"
       "  poly test [" (s/key "ARGS" cm) "]\n"
       "\n"
       "  The brick tests are executed from all projects they belong to except for the development\n"
       "  project (if not " (s/key ":dev" cm) " is passed in):\n"
       "\n"

       "  ARGS              Tests to execute\n"
       "  ----------------  -------------------------------------------------------------\n"
       "  (empty)           All brick tests that are directly or indirectly changed.\n"
       "\n"
       "  " (s/key ":project" cm) "          All brick tests that are directly or indirectly changed +\n"
       "                    tests for changed projects.\n"
       "\n"
       "  " (s/key ":all-bricks" cm) "       All brick tests.\n"
       "\n"
       "  " (s/key ":all" cm) "              All brick tests + all project tests (except development).\n"
       "\n"
       "\n"
       "  To execute the brick tests from the development project, also pass in :dev:\n"
       "\n"
       "  ARGS              Tests to execute\n"
       "  ----------------  -------------------------------------------------------------\n"
       "  " (s/key ":dev" cm) "              All brick tests that are directly or indirectly changed,\n"
       "                    executed from all projects (development included).\n"
       "\n"
       "  " (s/key ":project :dev" cm) "     All brick tests that are directly or indirectly changed,\n"
       "                    executed from all projects (development included) +\n"
       "                    tests for changed projects (development included).\n"
       "\n"
       "  " (s/key ":all-bricks :dev" cm) "  All brick tests, executed from all projects\n"
       "                    (development included).\n"
       "\n"
       "  " (s/key ":all :dev" cm) "         All brick tests, executed from all projects\n"
       "                    (development included) + all project tests\n"
       "                    (development included).\n"
       "\n"
       "  Projects can also be explicitly selected with e.g. " (s/key "project:proj1" cm) " or\n"
       "  " (s/key "project:proj1:proj2" cm) ".\n"
       "\n"
       "  We can also specify which bricks to include, by listing them like this:\n"
       "  brick:" (color/component "mycomponent" cm) ":" (color/component "another-component" cm) ":" (color/base "mybase" cm) "\n"
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
