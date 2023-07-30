(ns ^:no-doc polylith.clj.core.help.libs
     (:require [polylith.clj.core.help.shared :as s]
               [polylith.clj.core.system.interface :as system]
               [polylith.clj.core.util.interface.color :as color]))

(defn help [extended? cm]
  (str "  Shows all libraries that are used in the workspace.\n"
       "\n"
       "  poly libs [" (s/key ":compact" cm) "] [" (s/key ":outdated" cm) "]"
       (if extended?
         (str " [out:" (s/key  "FILENAME" cm) "]\n")
         "\n")
       "    " (s/key ":compact" cm) "  = Shows the table in a more compact way.\n"
       "    " (s/key ":outdated" cm) " = Shows the latest version of each library, or blank if up to date.\n"
       (if extended?
            (str "    out       = Creates a text or image file based on the output. If " (s/key "FILENAME" cm) "\n"
                 "                ends with .txt, then the file will contain the output as text.\n"
                 "                If FILENAME ends with .bmp, .wbmp, .gif, .jpeg, .jpg, .png, .tif,\n"
                 "                or .tiff, then the file will be generated as an image.\n")
            "")
       "\n"
       "                                                                                 " (color/component "u  u\n" cm)
       "                                                                                 " (color/component "s  t\n" cm)
       "                                                                                 " (color/component "e  i\n" cm)
       "    library                 version    type      KB   " (s/key "cl   dev  default  admin" cm) (color/component "   r  l\n" cm)
       "    -----------------------------------------------   --   -------------------   ----\n"
       "    antlr/antlr             2.7.7      maven    434   " (s/key "x     x      -       -" cm) "     .  x\n"
       "    clj-time                0.15.2     maven     23   " (s/key "x     x      -       -" cm) "     x  .\n"
       "    org.clojure/clojure     1.10.1     maven  3,816   " (s/key "x     x      -       -" cm) "     .  .\n"
       "    org.clojure/tools.deps  0.16.1264  maven     46   " (s/key "x     x      -       -" cm) "     .  .\n"
       "\n"
       "  In this example we have four libraries used by the " (color/project "cl" cm) " and " (color/project "dev" cm) " projects.\n"
       "  If any of the libraries are added to the " (color/profile "default" cm) " or " (color/profile "admin" cm) " profiles, they will appear\n"
       "  as an " (color/project "x" cm) " in these columns. Remember that src and test sources live together in a profile,\n"
       "  which is fine because they are only used from the development project.\n"
       "\n"
       "  The " (color/project "x" cm) " for the " (color/project "cl" cm) " and " (color/project "dev" cm) " columns says that the library is part of the src scope.\n"
       "  If a library is only used from the test scope, then it's marked with a 't'. A library\n"
       "  used in the test scope, can either be specified directly by the project itself via\n"
       "  " (color/project ":aliases > :test > :extra-deps" cm) " or indirectly via included bricks in " (color/project ":deps > :local/root" cm) "\n"
       "  which will be picked up and used by the 'test' command.\n"
       "\n"
       "  The " (color/project "x" cm) " in the " (color/component "user" cm) " column, tells that " (color/library "clj-time" cm) " is used by that component\n"
       "  by having it specified in its 'deps.edn' file as a src dependency.\n"
       "  If a dependency is only used from the test scope, then it will turn up as a " (color/project "t" cm) ".\n"
       "\n"
       "  Libraries can also be selected per project and it's therefore possible to have\n"
       "  different versions of the same library in different projects (if needed).\n"
       "  Use the " (s/key ":override-deps" cm) " key in the project's 'deps.edn' file to explicitly set\n"
       "  a version for one or several libraries in a project.\n"
       "\n"
       "  The 'type' column says in what way the dependency is included:\n"
       "   - maven, e.g.: clj-time/clj-time {" (s/key ":mvn/version" cm) " " (color/yellow cm "\"0.15.2\"") "}\n"
       "   - local, e.g.: clj-time {" (s/key ":local/root" cm) " " (color/yellow cm "\"/local-libs/clj-time-0.15.2.jar\"") "}\n"
       "   - git,   e.g.: clj-time/clj-time {" (s/key ":git/url" cm) " " (color/yellow cm "\"https://github.com/clj-time/clj-time.git\"") "\n"
       "                                     " (s/key ":sha" cm) "     " (color/yellow cm "\"d9ed4e46c6b42271af69daa1d07a6da2df455fab\"") "}\n"
       "\n"
       "  The KB column shows the size in kilobytes, which is the size of the jar\n"
       "  file for Maven and Local dependencies, and the size of all files in the\n"
       "  ~/.gitlibs/libs/YOUR-LIBRARY directory for Git dependencies.\n"
       "\n"
       "  Example:\n"
       "    poly libs\n"
       "    poly libs :compact\n"
       "    poly libs :outdated"

       (if extended?
         (str "\n    poly libs out:libs.png"
              "\n    poly libs out:libs.txt")

         "")))

(defn print-help [extended? color-mode]
  (println (help extended? color-mode)))

(comment
  (print-help false "dark")
  (print-help true "dark"))
