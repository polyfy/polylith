(ns dev.dev-common
  (:require [polylith.clj.core.command.interface :as command]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn ws-from-file [filename]
  (let [input (user-input/extract-params ["ws" (str "ws-file:" filename)])]
    (command/read-workspace "." input)))

(defn dir [ws-dir]
  (user-input/extract-params ["info" (str "ws-dir:" ws-dir) ":project" "project:dev:poly"]))
