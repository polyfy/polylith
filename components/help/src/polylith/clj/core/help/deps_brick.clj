(ns ^:no-doc polylith.clj.core.help.deps-brick
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.util.interface.color :as color]))

(defn help [extended? cm]
  (str "  Shows dependencies for selected brick.\n"
       "\n"
       "  poly deps brick:" (s/key "BRICK" cm) " [out:" (s/key "FILENAME" cm) "]\n"
       "\n"
       "    " (s/key "BRICK" cm) " = The name of the brick to show dependencies for.\n"
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
       "  " (color/green cm "payer") "                " (color/yellow cm "util") "\n"
       "\n"
       "  In this example, " (color/green cm "user") " is used by " (color/green cm "payer") " and it uses " (color/yellow cm "util") " itself.\n"
       "  If a brick or interface ends with '(t)' then it indicatest that\n"
       "  it's only used from the test context.\n"
       "\n"
       "  Example:\n"
       "    poly deps brick:mybrick\n"
       "    poly deps brick:mybrick out:deps.txt"
       (if extended?
         "\n    poly deps brick:mybrick out:deps.png"
         "")))

(defn print-help [extended? color-mode]
  (println (help extended? color-mode)))

(comment
  (print-help false "dark")
  (print-help true "dark")
  #__)
