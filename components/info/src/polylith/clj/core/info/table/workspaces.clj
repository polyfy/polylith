(ns ^:no-doc polylith.clj.core.info.table.workspaces
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn name-cell [index {:keys [alias]} alias->name]
  (text-table/cell 1 (+ 3 index) (alias->name alias)))

(defn alias-cell [index {:keys [alias]}]
  (text-table/cell 3 (+ 3 index) alias :purple))

(defn path-cell [index {:keys [ws-relative-dir]}]
  (text-table/cell 5 (+ 3 index) ws-relative-dir))

(defn table [{:keys [settings workspaces]}]
  (if (empty? workspaces)
    []
    (let [color-mode (:color-mode settings)
          alias->name (into {} (map (juxt :alias :name) workspaces))
          header-cells [(text-table/cell 1 "workspace")
                        (text-table/cell 3 "alias")
                        (text-table/cell 5 "path")]
          space-cells (text-table/spaces 1 [2 4] (repeat "  "))
          name-cells (map-indexed #(name-cell %1 %2 alias->name) workspaces)
          alias-cells (map-indexed alias-cell workspaces)
          path-cells (map-indexed path-cell workspaces)
          cells (concat header-cells
                        space-cells
                        name-cells
                        alias-cells
                        path-cells)
          line-cells (text-table/line 2 cells)
          empty-line-row (+ 3 (count workspaces))
          empty-line [(text-table/empty-line empty-line-row)]]
      (text-table/table "  " color-mode
                        empty-line cells line-cells))))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))

(comment
  (def workspace (-> (dev.dev-common/dir "examples/multiple-workspaces2/backend")
                     (polylith.clj.core.workspace.interface/workspace)))
  (print-table workspace)
  (table workspace)
  #__)
