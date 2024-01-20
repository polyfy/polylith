(ns ^:no-doc polylith.clj.core.workspace.text-table.ws-table
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.workspace.text-table.profile :as profile]
            [polylith.clj.core.workspace.text-table.ws-table-column.ifc-column :as ifc-column]
            [polylith.clj.core.workspace.text-table.ws-table-column.brick-column :as brick-column]
            [polylith.clj.core.workspace.text-table.ws-table-column.loc-columns :as loc-columns]
            [polylith.clj.core.workspace.text-table.ws-table-column.profile-columns :as profile-columns]
            [polylith.clj.core.workspace.text-table.ws-table-column.project-columns :as proj-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn table [{:keys [settings profiles projects components bases paths changes]} is-show-loc is-show-resources]
  (let [{:keys [color-mode thousand-separator]} settings
        n#dev (count (filter :is-dev projects))
        inactive-profiles (if (zero? n#dev) [] (profile/inactive-profiles settings profiles))
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        space-columns (range 2 (* 2 (+ 2 (count projects) (count inactive-profiles) (if is-show-loc 2 0))) 2)
        spaces (concat (repeat (-> space-columns count dec) "  ") (if is-show-loc [" "] ["  "]))
        profile-start-column (+ 5 (* 2 (count projects)))
        loc-start-column (+ profile-start-column (* 2 (count inactive-profiles)))
        ifc-column (ifc-column/column sorted-components bases)
        brick-column (brick-column/column bricks changes color-mode)
        project-columns (proj-columns/columns projects bricks paths is-show-loc is-show-resources thousand-separator)
        profile-columns (profile-columns/columns profile-start-column bricks inactive-profiles paths is-show-resources)
        loc-columns (loc-columns/columns is-show-loc bricks loc-start-column thousand-separator)
        header-spaces (text-table/spaces 1 space-columns spaces)
        cells (text-table/merge-cells ifc-column brick-column project-columns profile-columns loc-columns header-spaces)
        line (text-table/line 2 cells)
        section1 (if (or (zero? n#dev)
                         (= 1 (count projects)))
                   [] [(* 2 (+ (-> projects count) n#dev))])
        section2 (if is-show-loc [(- (last space-columns) 2)] [])
        line-space (text-table/spaces 2 (concat [4] section1 section2) (repeat "   "))]
    (text-table/table "  " color-mode cells line line-space)))

(defn print-table [workspace is-show-loc is-show-resources]
  (text-table/print-table (table workspace is-show-loc is-show-resources)))

(comment
  (require '[dev.jocke :as dev])
  (print-table dev/workspace false false)
  #__)
