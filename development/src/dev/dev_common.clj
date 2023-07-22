(ns dev.dev-common
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn ws-from-file [filename]
  (let [input (user-input/extract-params ["info" (str "ws-file:" filename)])]
    (command/read-workspace filename input)))

(defn dir [ws-dir & args]
  (user-input/extract-params (concat ["info" (str "ws-dir:" ws-dir) "replace:/Users/joakimtengstrand:USER-HOME" "skip:dev:api"]
                                     args)))
