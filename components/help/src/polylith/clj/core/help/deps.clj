(ns ^:no-doc polylith.clj.core.help.deps
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.help.deps-project :as deps-project]
            [polylith.clj.core.help.deps-brick :as deps-brick]
            [polylith.clj.core.help.deps-workspace :as deps-workspace]
            [polylith.clj.core.help.deps-brick-project :as deps-brick-project]))

(defn help [extended? cm]
  (str "  Shows dependencies.\n"
       "\n"
       "  poly deps [project:" (s/key "PROJECT" cm) "] [brick:" (s/key "BRICK" cm) "] [out:" (s/key "FILENAME" cm) "]\n"
       "\n"
       "    (omitted) = Shows workspace dependencies.\n"
       "\n"
       "    " (s/key "PROJECT" cm) "   = Shows dependencies for the given project.\n"
       "\n"
       "    " (s/key "BRICK" cm) "     = Shows dependencies for the given brick.\n"
       "\n"
       (if extended?
         (str "    " (s/key "FILENAME" cm) "  = The name of the text or image file to create, containing the\n"
              "                output from this command. If " (s/key "FILENAME" cm) " ends with .bmp, .wbmp,\n"
              "                .gif, .jpeg, .jpg, .png, .tif, or .tiff, then the file will\n"
              "                be generated as an image, otherwise as text.\n")
         (str "    " (s/key "FILENAME" cm) "  = The name of the text file to create, containing the\n"
              "                output from this command.\n"))
       "\n"
       "  To get help for a specific diagram, type: \n"
       "    poly help deps " (s/key "ARGS" cm) ":\n"
       "\n"
       "      " (s/key "ARGS" cm) " = " (s/key ":brick" cm) "           Help for the brick diagram.\n"
       "             " (s/key ":project" cm) "         Help for the project diagram.\n"
       "             " (s/key ":project :brick" cm) "  Help for the project/brick diagram.\n"
       "             " (s/key ":workspace" cm) "       Help for the workspace diagram.\n"
       "\n"
       "  Example:\n"
       "    poly deps\n"
       "    poly deps brick:mybrick\n"
       "    poly deps project:myproject\n"
       "    poly deps project:myproject brick:mybrick\n"
       "    poly deps out:deps.txt"
       (if extended?
         "\n    poly deps out:deps.png"
         "")))

(defn print-help [is-show-project is-show-brick is-show-workspace extended? color-mode]
  (cond
    (and is-show-project is-show-brick) (deps-brick-project/print-help extended? color-mode)
    is-show-project (deps-project/print-help extended? color-mode)
    is-show-brick (deps-brick/print-help extended? color-mode)
    is-show-workspace (deps-workspace/print-help extended? color-mode)
    :else (println (help extended? color-mode))))

(comment
  (println (help false "dark"))
  (println (help true "dark"))
  #__)
