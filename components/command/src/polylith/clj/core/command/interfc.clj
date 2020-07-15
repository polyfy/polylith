(ns polylith.clj.core.command.interfc
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [workspace cmd arg1 arg2]
  (core/execute workspace cmd arg1 arg2))
