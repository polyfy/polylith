(ns ^:no-doc polylith.clj.core.command.info
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.info.interface :as info]))

(defn info [workspace unnamed-args]
  (let [{:keys [ok? message]} (common/validate-args unnamed-args "info project:dev")]
    (if ok?
     (info/print-info workspace)
     (println message))))
