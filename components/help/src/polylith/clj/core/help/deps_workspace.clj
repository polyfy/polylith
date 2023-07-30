(ns ^:no-doc polylith.clj.core.help.deps-workspace
  (:require [polylith.clj.core.help.shared :as s]
            [polylith.clj.core.util.interface.color :as color]))

(defn help [extended? cm]
  (str "  Shows dependencies for the workspace.\n"
       "\n"
       "  poly deps [out:" (s/key "FILENAME" cm) "]\n"
       "\n"
       (if extended?
         (str "    " (s/key "FILENAME" cm) " = The name of the text or image file to create, containing the\n"
              "               output from this command. If " (s/key "FILENAME" cm) " ends with .bmp, .wbmp,\n"
              "               .gif, .jpeg, .jpg, .png, .tif, or .tiff, then the file will\n"
              "               be generated as an image, otherwise as text.\n")
         (str "    " (s/key "FILENAME" cm) " = The name of the text file to create, containing the\n"
              "               output from this command.\n"))
       "\n"
       "         " (color/yellow cm "p      \n")
       "         " (color/yellow cm "a  u  u\n")
       "         " (color/yellow cm "y  s  t\n")
       "         " (color/yellow cm "e  e  i\n")
       "  brick  " (color/yellow cm "r  r  l\n")
       "  --------------\n"
       "  " (color/green cm "payer") "  .  x  t\n"
       "  " (color/green cm "user") "   .  .  x\n"
       "  " (color/green cm "util") "   .  .  .\n"
       "  " (color/blue cm "cli") "    x  .  .\n"
       "\n"
       "  In this example, " (color/green cm "payer") " uses " (color/yellow cm "user") " from the src context, and " (color/yellow cm "util") " from\n"
       "  the test context (indicated by 't'). " (color/green cm "user") " uses " (color/yellow cm "util") " and " (color/blue cm "cli") " uses " (color/yellow cm "payer") ".\n"
       "  Each usage comes from at least one " (color/purple cm ":require") " statement in the brick.\n"
       "\n"
       "  Example:\n"
       "    poly deps\n"
       "    poly deps out:deps.txt"
       (if extended?
         "\n    poly deps out:deps.png"
         "")))

(defn print-help [extended? color-mode]
  (println (help extended? color-mode)))

(comment
  (print-help false "dark")
  (print-help true "dark")
  #__)
