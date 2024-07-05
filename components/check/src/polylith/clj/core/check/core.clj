(ns ^:no-doc polylith.clj.core.check.core
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.interface :as validator]))

(defn check [{:keys [messages]}]
  (let [error-messages (validator/error-messages messages)
        ok? (empty? error-messages)]
    {:ok? ok?
     :error-messages error-messages}))

(defn print-check [{:keys [messages] :as workspace} color-mode]
  (if (empty? messages)
   (println (color/ok color-mode "OK"))
   (validator/print-messages workspace)))

