(ns polylith.cli.cmd.help
  (:require [polylith.common.interface.color :as color]
            [clojure.string :as str]))

(defn command
  ([cmd]
   (str "  " (color/as-grey-light cmd)))
  ([cmd & args]
   (str "  " (color/as-grey-light cmd) " " (str/join " " args))))

(defn environment [cmd env]
  (str "  " (color/as-grey-light cmd) " " (color/environment env)))

(def help-text
  (str
    "Commands:\n"
    (command "check") "      Checks that the workspace is valid.\n"
    (environment "test" "env") "   Runs the tests for the given " (color/environment "environment") ".\n"
    (command "ws") "         Views current workspace.\n"
    (command "[help]") "     Views this help.\n"))

(defn execute []
  (println help-text))
