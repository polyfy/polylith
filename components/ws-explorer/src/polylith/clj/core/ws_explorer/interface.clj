(ns polylith.clj.core.ws-explorer.interface
  (:require [polylith.clj.core.ws-explorer.core :as core]))

(defn extract [workspace get]
  (core/extract workspace get))

(defn ws [workspace get out color-mode]
  "Prints or writes the workspace to standard output or disk."
  (core/ws workspace get out color-mode))
