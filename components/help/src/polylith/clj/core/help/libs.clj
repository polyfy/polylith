(ns polylith.clj.core.help.libs
     (:require [polylith.clj.core.help.shared :as s]
               [polylith.clj.core.util.interface.color :as color]))

(defn help [cm]
  (str "  Shows all libraries that are used in the workspace.\n"
       "\n"
       "  poly libs [:all]\n"
       "    :all = View all bricks, including those without library dependencies.\n"
       "                                                                              " (color/component "u  u\n" cm)
       "                                                                              " (color/component "s  t\n" cm)
       "                                                                              " (color/component "e  i\n" cm)
       "    library                       version     KB   " (s/key "cl   dev  default  admin" cm) (color/component "   r  l\n" cm)
       "    --------------------------------------------   --   -------------------   ----\n"
       "    antlr/antlr                   2.7.7      434   " (s/key "x-   x-      -       -" cm) "     ·  x\n"
       "    clj-time                      0.15.2      23   " (s/key "x-   x-      -       -" cm) "     x  ·\n"
       "    org.clojure/clojure           1.10.1   3,816   " (s/key "x-   x-      -       -" cm) "     ·  ·\n"
       "    org.clojure/tools.deps.alpha  0.8.695     46   " (s/key "x-   x-      -       -" cm) "     ·  ·\n"
       "\n"
       "  In this example we have four libraries used by the " (color/project "cl" cm) " and " (color/project "dev" cm) " projects.\n"
       "  If any of the libraries are added to the " (color/profile "default" cm) " or " (color/profile "admin" cm) " profiles, they will appear\n"
       "  as 'x' in these columns.\n"
       "\n"
       "  The x in 'x-' for the " (color/project "cl" cm) " and " (color/project "dev" cm) " columns says that the library is added to the\n"
       "  src code. The - in 'x-' says that the library isn't added to the test code.\n"
       "  A library can either be specified directly by the project itself, or indirectly via\n"
       "  components and bases.\n"
       "\n"
       "  The 'x' in the " (color/component "user" cm) " column, tells that " (color/library "clj-time" cm) " is used by that component\n"
       "  by having it specified in its 'deps.edn' file as a src dependency.\n"
       "\n"
       "  Libraries can also be selected per project and it's therefore possible to have different\n"
       "  versions of the same library in different projects (if needed).\n"
       "\n"
       "  This table supports all three different ways of including a dependency:\n"
       "   - Maven, e.g.: clj-time/clj-time {" (s/key ":mvn/version" cm) " \"0.15.2\"}\n"
       "   - Local, e.g.: clj-time {" (s/key ":local/root" cm) " \"/local-libs/clj-time-0.15.2.jar\"}\n"
       "   - Git, e.g.: {" (s/key ":git/url" cm) " \"https://github.com/clj-time/clj-time.git\"\n"
       "                 " (s/key ":sha" cm) "     \"d9ed4e46c6b42271af69daa1d07a6da2df455fab\"}\n"
       "\n"
       "  The KB column shows the size in kilobytes, which is the size of the jar\n"
       "  file for Maven and Local dependencies, and the size of all files in the\n"
       "  ~/.gitlibs/libs/YOUR-LIBRARY directory for Git dependencies."))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark"))
