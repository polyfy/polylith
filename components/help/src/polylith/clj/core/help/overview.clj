(ns polylith.clj.core.help.overview
  (:require [polylith.clj.core.help.shared :as s]))

(defn help-text [cm]
  (str "  Shows the output from the info, deps, and libs commands, side by side.\n"
       "  This command is mainly used to generate an image for your documentation.\n"
       "\n"
       "  poly overview [:no-changes] [out:" (s/key "FILENAME" cm) "]\n"
       "    (omitted)   = Shows the output.\n"
       "    :no-changes = Shows the output as if there were no changes in the workspace.\n"
       "    out         = Creates a text or image file based on the output.\n"
       "                  If " (s/key "FILENAME" cm) " ends with .txt, then the file will contain\n"
       "                  the output as text. If FILENAME ends with .bmp, .jpg, or .png,\n"
       "                  then the file will be generated as an image."))

(defn print-help [color-mode]
  (println (help-text color-mode)))
