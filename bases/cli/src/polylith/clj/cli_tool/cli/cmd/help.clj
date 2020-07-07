(ns polylith.clj.cli-tool.cli.cmd.help
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc.color :as color]))

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
    (command "info" color-mode) " [" (arg "-dump" color-mode) "]  Views information about current workspace.\n"
    (test-cmd color-mode) "    Runs the tests for the given environment (or all).\n"
    (help-cmd color-mode) "        Views this help."))

(defn execute [color-mode]
  (-> color-mode help-text println))
