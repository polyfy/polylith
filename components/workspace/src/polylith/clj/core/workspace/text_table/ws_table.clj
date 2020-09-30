(ns polylith.clj.core.workspace.text-table.ws-table
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.workspace.text-table.profile :as profile]
            [polylith.clj.core.workspace.text-table.ws-table-column.ifc-column :as ifc-column]
            [polylith.clj.core.workspace.text-table.ws-table-column.brick-column :as brick-column]
            [polylith.clj.core.workspace.text-table.ws-table-column.loc-columns :as loc-columns]
            [polylith.clj.core.workspace.text-table.ws-table-column.profile-columns :as profile-columns]
            [polylith.clj.core.workspace.text-table.ws-table-column.env-columns :as env-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn table [{:keys [settings environments components bases paths changes user-input]} is-show-loc show-resources?]
  (let [{:keys [color-mode thousand-sep]} settings
        profiles (profile/inactive-profiles settings user-input)
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        space-columns (range 2 (* 2 (+ 2 (count environments) (count profiles) (if is-show-loc 2 0))) 2)
        spaces (concat (repeat (-> space-columns count dec) "  ") (if is-show-loc [" "] ["  "]))
        profile-start-column (+ 5 (* 2 (count environments)))
        loc-start-column (+ profile-start-column (* 2 (count profiles)))
        ifc-column (ifc-column/column sorted-components bases)
        brick-column (brick-column/column bricks changes color-mode)
        env-columns (env-columns/columns environments bricks paths changes is-show-loc show-resources? thousand-sep)
        profile-columns (profile-columns/columns profile-start-column bricks profiles paths settings show-resources?)
        loc-columns (loc-columns/columns is-show-loc bricks loc-start-column thousand-sep)
        header-spaces (text-table/spaces 1 space-columns spaces)
        cells (text-table/merge-cells ifc-column brick-column env-columns profile-columns loc-columns header-spaces)
        line (text-table/line 2 cells)
        section1 (if (= 1 (count environments)) [] [(* 2 (-> environments count inc))])
        section2 (if is-show-loc [(- (last space-columns) 2)] [])
        line-space (text-table/spaces 2 (concat [4] section1 section2) (repeat "   "))]
    (text-table/table "  " color-mode cells line line-space)))

(defn print-table [workspace is-show-loc show-resources?]
  (text-table/print-table (table workspace is-show-loc show-resources?)))
