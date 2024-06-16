(ns ^:no-doc polylith.clj.core.validator.m110-invalid-config-file)

(defn error [message]
  {:type "error"
   :code 110
   :message message
   :colorized-message message})

(defn errors [current-ws-errors]
  (vec (map #(-> % :error error)
            current-ws-errors)))
