(ns ^:no-doc polylith.clj.core.command.interface
  (:require [polylith.clj.core.command.core :as core]))

(defn execute-command [user-input]
  (core/execute user-input))
