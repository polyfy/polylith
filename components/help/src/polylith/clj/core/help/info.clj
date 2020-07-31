(ns polylith.clj.core.help.info
  (:require [polylith.clj.core.help.shared :as shared]
            [polylith.clj.core.util.interfc.color :as color]))

(defn help-text [color-mode]
  (let [envs (color/namespc "environments/" color-mode)
        env (color/environment "env" color-mode)
        env-ns (str envs env)]

    (str

         "  The first table shows the defined environments with these columns:\n"
         "    environment:  The name of the environment which is a directory under 'environments' at the root.\n\n"

         "    alias:        The name of the alias. The alias is either calculated based on the environment name\n"
         "                  or defined in 'deps.edn' at the root in the :env-aliases key, e.g.\n"
         "                  " (color/grey color-mode "{:polylith {:env-aliases {\"development\" \"dev\"}}}") "\n"
         "                  The aliases are are used in the second table to save space.\n\n"

         "    src:          Contains three characters.\n"
         "                    First:\n"
         "                      'x' = The environment has a 'src' folder at its root, or '-' if not.\n"
         "                    Second:\n"
         "                      'x' = The environment has a 'test' folder at its root, or '-' if not.\n"
         "                    Third:\n"
         "                      'x' = At least one file has changed since the last commit in git, in the environment's\n"
         "                            directory " env-ns " which mark the tests for execution.\n"
         "                      '-' = No tests will be executed for the environment.\n\n"

         "  The second table shows the following columns (where 'brick' is either a " (color/component "component" color-mode) " or a " (color/base "base" color-mode) "):\n"
         "    " (color/interface "interface" color-mode) "     Interfaces are " (color/interface "yellow" color-mode) " and get their names from the first namespace that follows the top namespace,\n"
         "                  e.g. " (shared/interface-ns "interface" color-mode) ". An interface can be implemented by more than one component.\n\n"

         "    " (color/component "component" color-mode) "     Components are " (color/component "green" color-mode) " and get their names from directories in the 'components' root directory.\n\n"

         "    " (color/base "base" color-mode) "          Bases are " (color/base "blue" color-mode) " and get their names from directories in the 'bases' root directory.\n\n"

         "    " (color/environment "environment" color-mode)  "   Environments are " (color/environment "purple" color-mode) " and get their names from directories in the 'environments' root directory.\n"
         "                  Each environment column has three characters.\n"
         "                    First:\n"
         "                      'x' = The brick's 'src' folder is part of the environment, or '-' if not.\n"
         "                    Second:\n"
         "                      'x' = The brick's 'test' folder is part of the environment, or '-' if not.\n"
         "                    Third:\n"
         "                      'x' = The tests are marked to be executed. This happens when the brick is\n"
         "                            either directly or indirectly changed since the last commit in git.\n"
         "                      '-' = The tests will not be executed.\n\n"

         "    loc           The number of lines of code the brick has in its namespaces under the 'src' directory.\n\n"

         "    (t)           The number of lines of code the brick has in its namespaces under the 'test' directory.\n\n"

         "  If the 'check' command produces any errors or warnings, then they are shown after the table.\n"
         "  If the 'test' command is executed, it will execute test for all directly and indirectly changed environments.\n\n"

         "  poly info [ARG]\n"
         "    ARG = (omitted) -> Shows basic information.\n"
         "          -loc      -> Also shows the two last columns 'loc' and '(t)' where the first one is the number of\n"
         "                       lines of code the brick has in its namespaces under the 'src' directory while the second\n"
         "                       shows the number of lines of code under the 'test' directory.\n"
         "          -dump     -> Shows the internal workspace data structure.\n\n"

         "  Examples:\n"
         "    poly info\n"
         "    poly info -loc\n"
         "    poly info -dump")))

(defn print-help [color-mode]
  (println (help-text color-mode)))
