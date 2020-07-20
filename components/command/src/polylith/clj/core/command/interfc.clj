(ns polylith.clj.core.command.interfc
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [ws-path workspace cmd arg1 arg2 arg3]
  (core/execute ws-path workspace cmd arg1 arg2 arg3))
