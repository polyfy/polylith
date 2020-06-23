(ns polylith.cli.cmd.help
  (:require [polylith.common.interface.color :as color]
            [clojure.string :as str]))

(defn command
  ([cmd]
   (str "  " (color/as-green cmd)))
  ([cmd & args]
   (str "  " (color/as-green cmd) " "
        (color/as-yellow (str/join " " args)))))

(def help-text
  (str
    "Commands:\n"
    (command "check") "      Checks that the workspace is valid, such as:\n"
    "              - component interfaces conform to their contracts\n"
    "              - there are no circular dependencies\n"
    "              - bases don't share names with components or interfaces\n"
    "\n"
    (command "test" "env") "   Runs the tests for the given environment.\n"
    "\n"
    (command "ws") "         Views current workspace.\n"
    "\n"
    (command "help") "       Views this help.\n"
    "\n"))

(defn execute []
  (println help-text))
