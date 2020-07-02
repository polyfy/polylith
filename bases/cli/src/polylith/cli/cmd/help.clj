(ns polylith.cli.cmd.help
  (:require [polylith.util.interface.color :as color]
            [clojure.string :as str]))

(defn command
  ([cmd color-mode]
   (str "  " (color/grey color-mode cmd)))
  ([cmd color-mode & args]
   (str "  " (color/grey color-mode cmd) " " (str/join " " args))))

(defn arg [cmd color-mode]
  (color/grey color-mode cmd))

(defn environment [cmd env color-mode]
  (str "  " (color/grey color-mode cmd) " " (color/environment env color-mode)))

(defn help-cmd [color-mode]
  (str "  [" (color/grey color-mode "help") "]"))

(defn test-cmd [color-mode]
  (let [cmd (color/grey color-mode "test")
        env (color/environment "env" color-mode)]
    (str "  " cmd " [" env "]")))

(defn help-text [color-mode]
  (str
    (command "check" color-mode) "         Checks that the workspace is valid.\n"
    (test-cmd color-mode) "    Runs the tests for the given environment (or all).\n"
    (command "ws" color-mode) "   [" (arg "-dump" color-mode) "]  Views current workspace.\n"
    (help-cmd color-mode) "        Views this help."))

(defn execute [color-mode]
  (-> color-mode help-text println))
