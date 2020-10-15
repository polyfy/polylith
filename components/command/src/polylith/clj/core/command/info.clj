(ns polylith.clj.core.command.info
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.workspace.interface :as ws]))

(defn info [workspace unnamed-args]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "info project:dev")]
    (if ok?
     (ws/print-info workspace)
     (println message))))
