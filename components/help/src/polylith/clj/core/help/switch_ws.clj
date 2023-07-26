(ns ^:no-doc polylith.clj.core.help.switch-ws
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Selects which workspace to be used by commands executed from the shell.\n"
       "  This replaces the use of ws-dir:" (s/key "DIR" cm) " and ws-file:" (s/key "FILE" cm) ", which will be appended\n"
       "  automatically when executing commands, if dir:" (s/key "DIR" cm) " or file:" (s/key "FILE" cm) " is given.\n"
       "\n"
       "  switch-ws " (s/key "ARG" cm) "\n"
       "    " (s/key "ARG" cm) " = dir:" (s/key "DIR" cm) "    Switches to the selected workspace directory.\n"
       "                     The prompt will be prefixed with 'dir:' to show this.\n"
       "          file:" (s/key "FILE" cm) "  Switches to the workspace specified in the selected file,\n"
       "                     created by something like 'poly ws out:ws.edn'.\n"
       "                     The prompt will be prefixed with 'file:' to show this.\n"
       "\n"
       "  Example:\n"
       "    switch-ws dir:~/myworkspace\n"
       "    switch-ws file:../../another/ws.edn"))

(defn print-help [color-mode]
  (println (help color-mode)))

(comment
  (print-help "dark")
  #__)
