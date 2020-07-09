(ns polylith.clj.core.command.interfc
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [workspace cmd arg]
  (core/execute-command workspace cmd arg))
