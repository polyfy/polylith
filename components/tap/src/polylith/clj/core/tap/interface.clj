(ns polylith.clj.core.tap.interface
  (:require [polylith.clj.core.tap.core :as core]))

(defn execute [cmd]
  (core/execute cmd))
