(ns polylith.cli.cmd.help
  (:require [polylith.common.interface.color :as color]
            [clojure.string :as str]))

(defn command
  ([cmd dark-mode?]
   (str "  " (color/as-grey dark-mode? cmd)))
  ([cmd dark-mode? & args]
   (str "  " (color/as-grey dark-mode? cmd) " " (str/join " " args))))

(defn environment [cmd env dark-mode?]
  (str "  " (color/as-grey dark-mode? cmd) " " (color/environment env)))

(defn help-text [dark-mode?]
  (str
    "Commands:\n"
    (command "check" dark-mode?) "      Checks that the workspace is valid.\n"
    (environment "test" "env" dark-mode?) "   Runs the tests for the given " (color/environment "environment") ".\n"
    (command "ws" dark-mode?) "         Views current workspace.\n"
    (command "[help]" dark-mode?) "     Views this help.\n"))

(defn execute [dark-mode?]
  (-> dark-mode? help-text println))
