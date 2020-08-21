(ns polylith.clj.core.command.info
  (:require [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.workspace.interfc :as ws]))

(defn info [workspace show-loc? show-resources-flag? unnamed-args]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "info env:dev")]
    (if ok?
     (ws/print-info workspace show-loc? show-resources-flag?)
     (println message))))
