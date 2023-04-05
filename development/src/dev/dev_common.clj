(ns dev.dev-common
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn ws-from-file [filename]
  (let [input (user-input/extract-params ["ws" (str "ws-file:" filename)])]
    (command/read-workspace "." input)))

(defn dir [ws-dir & args]
  (user-input/extract-params (concat ["info" (str "ws-dir:" ws-dir) "replace:/Users/joakimtengstrand:USER-HOME"]
                                     args)))
