(ns polylith.clj.core.command.interfc
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [ws-root-path workspace cmd arg1 arg2 arg3]
  (core/execute ws-root-path workspace cmd arg1 arg2 arg3))
