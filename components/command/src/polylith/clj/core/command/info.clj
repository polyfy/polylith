(ns polylith.clj.core.command.info
  (:require [polylith.clj.core.workspace.interfc :as ws]))

(defn validate [unnamed-args]
  (if (-> unnamed-args empty? not)
    {:message "  Arguments should be passed by name, e.g.: info :loc"}
    {:ok? true}))

(defn info [workspace loc unnamed-args]
  (let [{:keys [ok? message]} (validate unnamed-args)]
    (if ok?
     (ws/print-table workspace (= "true" loc))
     (println message))))
