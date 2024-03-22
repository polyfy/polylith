(ns ^:no-doc polylith.clj.core.help.switch-ws
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Selects which workspace to be used by commands executed from the shell.\n"
       "  After we have switched workspace, all subsequent commands will append either\n"
       "  ws-dir:" (s/key "DIR" cm) " or ws-file:" (s/key "FILE" cm) ", depending on whether we switch to a directory or a file.\n"
       "\n"
       "  switch-ws [dir:" (s/key "DIR" cm) "] [file:" (s/key "FILE" cm) "] [dir-via:" (s/key "DIRNAME" cm) "] [file-via:" (s/key "FILENAME" cm) "]\n"
       "\n"
       "    " (s/key "DIR" cm) " = Switches to the given workspace directory.\n"
       "          The prompt will be prefixed with 'dir:' to show this.\n"
       "\n"
       "    " (s/key "FILE" cm) " = Switches to the workspace specified in the selected file,\n"
       "           created by something like 'poly ws out:ws.edn'.\n"
       "           The prompt will be prefixed with 'file:' to show this.\n"
       "\n"
       "    " (s/key "DIRNAME" cm) " = Switches to the workspace specified with " (s/key ":dir" cm) " in\n"
       "              ~/.config/polylith/config.edn under the " (s/key ":ws-shortcuts" cm) " key.\n"
       "\n"
       "    " (s/key "FILENAME" cm) " = Switches to the workspace specified with " (s/key ":file" cm) " in\n"
       "               ~/.config/polylith/config.edn under the " (s/key ":ws-shortcuts" cm) " key.\n"
       "\n"
       "    Example of a config file in ~/.config/polylith/config.edn:\n"
       "    \n"
       "\n"
       "  Example:\n"
       "    switch-ws dir:~/myworkspace\n"
       "    switch-ws file:../../another/ws.edn\n"
       "    switch-ws dir-via:myws\n"
       "    switch-ws file-via:myfile"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
