(ns polylith.clj.core.workspace.text-table.info-tables
  (:require [polylith.clj.core.workspace.text-table.new-count-table :as count-table]
            [polylith.clj.core.workspace.text-table.new-env-table :as env-table]
            [polylith.clj.core.workspace.text-table.ws-table.core :as ws-table]))

(defn print-info [workspace show-loc? show-resources?]
  (count-table/print-table workspace)
  (println)
  (env-table/print-table workspace show-loc? show-resources?)
  (println)
  (ws-table/print-table workspace show-loc? show-resources?))
