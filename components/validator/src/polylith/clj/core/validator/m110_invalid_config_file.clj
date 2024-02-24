(ns ^:no-doc polylith.clj.core.validator.m110-invalid-config-file)

(defn error [message]
  {:type "error"
   :code 110
   :message message
   :colorized-message message})

(defn other-ws-error [{:keys [config-error alias]}]
  (error (str config-error ". Found in workspace with alias '" alias "'.")))

(defn errors [current-ws-errors workspaces]
  (vec (concat (map #(-> % :error error)
                    current-ws-errors)
               (map other-ws-error
                    (filter :config-error workspaces)))))
