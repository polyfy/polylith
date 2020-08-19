(ns polylith.clj.core.workspace.text-table.info-tables
  (:require [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.workspace.text-table.count-table :as count-table]
            [polylith.clj.core.workspace.text-table.env-table :as env-table]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(defn print-info [{:keys [messages] :as workspace} show-loc? show-resources?]
  (count-table/print-table workspace)
  (println)
  (env-table/print-table workspace show-loc? show-resources?)
  (println)
  (ws-table/print-table workspace show-loc? show-resources?)
  (when (-> messages empty? not)
    (println)
    (println (common/pretty-messages messages (-> workspace :settings :color-mode)))))
