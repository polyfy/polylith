(ns ^:no-doc polylith.clj.core.info.interface
  (:require [polylith.clj.core.info.table.workspace :as workspace]
            [polylith.clj.core.info.table.brick :as brick]
            [polylith.clj.core.info.table.project :as project]))

(defn print-info [workspace]
  (workspace/print-info workspace))

(defn brick-table [workspace is-show-loc is-show-resources]
  (brick/table workspace is-show-loc is-show-resources))

(defn project-table [workspace is-show-loc is-show-resources]
  (project/table workspace is-show-loc is-show-resources))

(defn workspace-table [workspace]
  (workspace/table workspace))
