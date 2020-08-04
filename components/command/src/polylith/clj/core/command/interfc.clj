(ns polylith.clj.core.command.interfc
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [ws-dir workspace cmd arg1 arg2]
  (core/execute ws-dir workspace cmd arg1 arg2))
