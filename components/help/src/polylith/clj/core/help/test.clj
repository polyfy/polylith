(ns polylith.clj.core.help.test
  (:require [polylith.clj.core.util.interfc.color :as color]))

(defn help-text [color-mode]
  (str "  Executes tests for the given environment or all if not given.\n"
       "  For every environment " (color/environment "env" color-mode) " in directory 'environments/" (color/environment "env" color-mode) "/deps.edn', it executes the tests\n"
       "  in the directories listed in ':aliases > :test > :extra-paths'. Put the unit tests \n"
       "  under each brick (componend/base) and the integration tests under each environment.\n"
       "  Each 'test' directory should be referred to relative to the 'deps.edn' file, e.g.\n"
       "  '../../components/" (color/component "mycomponent" color-mode) "/test' or just 'test' for environments\n"
       "  (to point to a 'environments/" (color/environment "env" color-mode) "/test' directory).\n\n"
       "  poly test [" (color/environment "env" color-mode) "]\n"
       "    " (color/environment "env" color-mode) " = (omitted) -> Run tests for all environments.\n"
       "          else      -> Run test for the given environment.\n\n"
       "  Examples:\n"
       "    poly test\n"
       "    poly test dev"))

(defn print-help [color-mode]
  (println (help-text color-mode)))
