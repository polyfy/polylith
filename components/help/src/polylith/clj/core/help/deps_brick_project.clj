(ns ^:no-doc polylith.clj.core.help.deps-brick-project
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.help.shared :as s]))

(defn help [extended? cm]
  (str "  Shows dependencies for selected brick and project.\n"
       "\n"
       "  poly deps project:" (s/key "PROJECT" cm) " brick:" (s/key "BRICK" cm) " [out:" (s/key "FILENAME" cm) "]\n"
       "\n"
       "    " (s/key "PROJECT" cm) "  = The project (name or alias) to show dependencies for.\n"
       "\n"
       "    " (s/key "BRICK" cm) "    = The brick to show dependencies for.\n"
       "\n"
       (if extended?
         (str "    " (s/key "FILENAME" cm) " = The name of the text or image file to create, containing the\n"
              "               output from this command. If " (s/key "FILENAME" cm) " ends with .bmp, .wbmp,\n"
              "               .gif, .jpeg, .jpg, .png, .tif, or .tiff, then the file will\n"
              "               be generated as an image, otherwise as text.\n")
         (str "    " (s/key "FILENAME" cm) " = The name of the text file to create, containing the\n"
              "               output from this command.\n"))
       "\n"
       "  used by  <  " (color/green cm "user") "  >  uses\n"
       "  -------              ----\n"
       "  " (color/green cm "payer") "                " (color/green cm "util") "\n"
       "\n"
       "  In this example, " (color/green cm "user") " is used by " (color/green cm "payer") " and it uses " (color/green cm "util") " itself.\n"
       "  If a brick ends with '(t)' then it indicatest that it's only used\n"
       "  from the test context.\n"
       "\n"
       "  Example:\n"
       "    poly deps project:myproject brick:mybrick\n"
       "    poly deps project:myproject brick:mybrick out:deps.txt"
       (if extended?
         "\n    poly deps project:myproject brick:mybrick out:deps.png"
         "")))

(defn print-help [extended? color-mode]
  (println (help extended? color-mode)))

(comment
  (print-help false "dark")
  (print-help true "dark")
  #__)
