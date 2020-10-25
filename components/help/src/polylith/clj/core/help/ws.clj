(ns polylith.clj.core.help.ws
  (:require [polylith.clj.core.help.shared :as s]))

(defn help [cm]
  (str "  Prints or writes the workspace as data.\n"
       "\n"
       "  poly ws [get:" (s/key "ARG" cm) "] [out:" (s/key "FILE" cm) "] \n"
       "    " (s/key "ARG" cm) " = keys  -> Lists the keys for the data structure:\n"
       "                   - If it's a hash map, it returns all its keys.\n"
       "                   - If it's a list and its elements are hash maps,\n"
       "                     it returns a list with all the " (s/key ":name" cm) " keys.\n"
       "\n"
       "          count -> Counts the number of elements.\n"
       "\n"
       "          " (s/key "KEY" cm) "   -> If applied to a hash map, it returns the value of the " (s/key "KEY" cm) ".\n"
       "                   If applied to a list of hash maps, it returns the hash map with\n"
       "                   a matching " (s/key ":name" cm) ". Projects are also matched against " (s/key ":alias" cm) ".\n"
       "\n"
       "          " (s/key "INDEX" cm) " -> A list element can be looked up by " (s/key "INDEX" cm) ".\n"
       "\n"
       "          Several " (s/key "ARG" cm) " keys can be given, separated by colon.\n"
       "          Every new key goes one level deeper into the workspace data structure.\n"
       "\n"
       "    " (s/key "FILE" cm) " = Writes the output to the specified " (s/key "FILE" cm) ". Will have the same effect\n"
       "           as setting " (s/key "color-mode:none" cm) " and piping the output to a file.\n"
       "\n"
       "  Example:\n"
       "    poly ws\n"
       "    poly ws get:keys\n"
       "    poly ws get:keys\n"
       "    poly ws get:count\n"
       "    poly ws get:settings\n"
       "    poly ws get:user-input:args\n"
       "    poly ws get:user-input:args:0\n"
       "    poly ws get:settings:keys\n"
       "    poly ws get:components:keys\n"
       "    poly ws get:components:count\n"
       "    poly ws get:components:mycomp:lines-of-code-src\n"
       "    poly ws out:ws.edn\n"
       "    poly ws color-mode:none > ws.edn"))

(defn print-help [color-mode]
  (println (help color-mode)))
