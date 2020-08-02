(ns polylith.clj.core.command.interfc
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [current-dir workspace cmd arg1 arg2]
  (core/execute current-dir workspace cmd arg1 arg2))
