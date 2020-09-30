(ns polylith.clj.core.help.libs
     (:require [polylith.clj.core.help.shared :as s]
               [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows all libraries that are used in the workspace.\n"
       "\n"
       "  poly libs\n"
       "                                                                       " (color/component "a  p\n" cm)
       "                                                                       " (color/component "d  a  u  u\n" cm)
       "                                                                       " (color/component "m  y  s  t" cm) (color/base "  c\n" cm)
       "                                                                       " (color/component "i  e  e  i" cm) (color/base "  l\n" cm)
       "    library                       version   " (s/key "cl   dev  default  admin" cm) (color/component "   n  r  r  l" cm) (color/base "  i\n" cm)
       "    -------------------------------------   --   -------------------   -------------\n"
       "    clj-time                      0.15.2    " (s/key "x     x      -       -" cm) "     ·  ·  x  ·  ·\n"
       "    org.clojure/clojure           1.10.1    " (s/key "x     x      -       -" cm) "     ·  ·  ·  ·  ·\n"
       "    org.clojure/tools.deps.alpha  0.8.695   " (s/key "x     x      -       -" cm) "     ·  ·  ·  ·  ·\n"
       "\n"
       "  In this example we have three libraries used by the " (color/environment "cl" cm) " and " (color/environment "dev" cm) " environments.\n"
       "  If any of the libraries are added to the " (color/profile "default" cm) " or " (color/profile "admin" cm) " profiles, they will appear\n"
       "  as 'x' in these columns.\n"
       "\n"
       "  The 'x' in the " (color/component "user" cm) " column, tells that 'clj-time' is used by that component\n"
       "  by having at least one " (s/key ":require" cm) " statement that includes the 'clj-time' namespace.\n"
       "\n"
       "  Libraries are only specified per environment, and the way it finds out which libraries\n"
       "  are used for a specific brick, is by looking in " (s/key ":ns-to-lib" cm) " in ./deps.edn\n"
       "  which in this case has the value {clj-time clj-time} - typed in as symbols.\n"
       "\n"
       "  Libraries are selected per envronment and it's therefore possible to have different\n"
       "  versions of the same library in different environments (if needed)."))

(defn print-help [color-mode]
  (println (help color-mode)))
