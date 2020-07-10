(ns polylith.clj.core.help.info
  (:require [polylith.clj.core.help.shared :as shared]
            [polylith.clj.core.util.interfc.color :as color]))

(defn help-text [color-mode]
  (str "  Shows a table with the following columns (where 'brick' is either a " (color/component "component" color-mode) " or a " (color/base "base" color-mode) "):\n"
       "    " (color/interface "interface" color-mode) "     Interfaces are " (color/interface "yellow" color-mode) " and get their names from the first namespace that follows the top namespace,\n"
       "                  e.g. " (shared/interface-ns "interface" color-mode) ". An interface can be implemented by more than one component.\n"
       "    " (color/component "component" color-mode) "     Components are " (color/component "green" color-mode) " and get their names from directories in the 'components' top directory.\n"
       "    " (color/base "base" color-mode) "          Bases are " (color/base "blue" color-mode) " and get their names from directories in the 'bases' top directory.\n"
       "    " (color/environment "environment" color-mode)  "   Environments are " (color/environment "purple" color-mode) " and get their names from directories in the 'environments' top directory.\n"
       "                  An 'x' means the brick is included in the environment while '-' means it's not.\n"
       "    loc           The number of lines of code the brick has in its namespaces under the 'src' directory.\n"
       "    (t)           The number of lines of code the brick has in its namespaces under the 'test' directory.\n\n"
       "  If the 'check' command produces any errors or warnings, then they are shown after the table.\n\n"
       "  poly info [ARG]\n"
       "    ARG = (omitted) -> Shows basic information.\n"
       "          -loc      -> Also shows the two last columns 'loc' and '(t)' where the first one is the number of\n"
       "                       lines of code the brick has in its namespaces under the 'src' directory while the second\n"
       "                       shows the number of lines of code under the 'test' directory.\n"
       "          -dump     -> Shows the internal workspace data structure.\n\n"
       "  Examples:\n"
       "    poly info\n"
       "    poly info -loc\n"
       "    poly info -dump"))

(defn print-help [color-mode]
  (println (help-text color-mode)))
