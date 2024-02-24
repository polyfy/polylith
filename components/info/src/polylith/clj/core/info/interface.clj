(ns ^:no-doc polylith.clj.core.info.interface
  (:require [polylith.clj.core.info.table.brick :as ws]
            [polylith.clj.core.info.table.workspace :as workspace]
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

(comment
  (require '[polylith.clj.core.workspace.interface :as ws])
  (require '[polylith.clj.core.user-input.interface :as user-input])
  (def input (user-input/extract-arguments ["info" "ws-dir:examples/multiple-workspaces2/backend"]))
  (def workspace (ws/workspace input))
  (into {} (map (juxt :alias :name) (:workspaces workspace)))
  (-> workspace :configs :workspace :workspaces)

  (print-info workspace)
  #__)
