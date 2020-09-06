(ns polylith.clj.core.ws-explorer.interface
  (:require [polylith.clj.core.ws-explorer.core :as core]))

(defn extract [workspace get]
  (core/extract workspace get))

(defn print-ws [workspace get]
  (core/print-ws workspace get))
