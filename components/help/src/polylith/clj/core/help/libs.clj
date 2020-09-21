(ns polylith.clj.core.help.libs
  (:require [polylith.clj.core.util.interface.color :as color]))

(defn help [color-mode]
  (str "  Shows all libraries that are used in the workspace.\n"
       "\n"
       "  Example:                                                             " (color/component "a  p\n" color-mode)
       "                                                                       " (color/component "d  a  u  u\n" color-mode)
       "                                                                       " (color/component "m  y  s  t" color-mode) (color/base "  c\n" color-mode)
       "                                                                       " (color/component "i  e  e  i" color-mode) (color/base "  l\n" color-mode)
       "    library                       version   " (color/purple color-mode "cl   dev  default  admin") (color/component "   n  r  r  l" color-mode) (color/base "  i\n" color-mode)
       "    -------------------------------------   --   -------------------   -------------\n"
       "    clj-time                      0.15.2    " (color/purple color-mode "x     x      -       -") "     ·  ·  x  ·  ·\n"
       "    org.clojure/clojure           1.10.1    " (color/purple color-mode "x     x      -       -") "     ·  ·  ·  ·  ·\n"
       "    org.clojure/tools.deps.alpha  0.8.695   " (color/purple color-mode "x     x      -       -") "     ·  ·  ·  ·  ·\n"
       "\n"
       "  In this example we have three libraries used by the " (color/environment "cl" color-mode) " and " (color/environment "dev" color-mode) " environments.\n"
       "  If any of the libraries have been added to the " (color/profile "default" color-mode) " or " (color/profile "admin" color-mode) " profiles,\n"
       "  they will appear as 'x' in these columns.\n"
       "\n"
       "  The 'x' in the " (color/component "user" color-mode) " column, tells that 'clj-time' is used by that component\n"
       "  by having at least one " (color/purple color-mode ":require") " statement that includes the 'clj-time' namespace.\n"
       "\n"
       "  Libraries are only specified per environment and the way it finds out which libraries\n"
       "  are used for a specific brick, is by looking in " (color/purple color-mode ":ns->lib") " in ./deps.edn,\n"
       "  which in this case has the value {clj-time clj-time} - typed in as symbols.\n"
       "\n"
       "  Libraries are selected per envronment and it's therefore possible to have different\n"
       "  version of the same library in different environments (if needed)."))

(defn print-help [color-mode]
  (println (help color-mode)))
