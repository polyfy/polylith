(ns polylith.clj.core.workspace.ws-table.core
  (:require [clojure.walk :as walk]
            [polylith.clj.core.text-table2.interfc :as text-table]
            [polylith.clj.core.workspace.ws-table.ifc-column :as ifc-column]
            [polylith.clj.core.workspace.ws-table.brick-column :as brick-column]
            [polylith.clj.core.workspace.ws-table.loc-columns :as loc-columns]
            [polylith.clj.core.workspace.ws-table.profile-columns :as profile-columns]
            [polylith.clj.core.workspace.ws-table.env-columns :as env-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn table [{:keys [settings environments components bases changes total-loc-src-bricks total-loc-test-bricks]} show-loc?]
  (let [{:keys [color-mode thousand-sep]} settings
        profiles (profile-columns/show-profiles settings)
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        space-columns (range 2 (* 2 (+ 2 (count environments) (count profiles) (if show-loc? 2 0))) 2)
        spaces (concat (repeat (-> space-columns count dec) "  ") (if show-loc? [" "] ["  "]))
        profile-start-column (+ 5 (* 2 (count environments)))
        loc-start-column (+ profile-start-column (* 2 (count profiles)))
        ifc-column (ifc-column/column sorted-components bases)
        brick-column (brick-column/column bricks changes color-mode)
        env-columns (env-columns/columns environments bricks changes settings show-loc? thousand-sep color-mode)
        profile-columns (profile-columns/columns profile-start-column bricks profiles settings)
        loc-columns (loc-columns/columns show-loc? bricks loc-start-column total-loc-src-bricks total-loc-test-bricks thousand-sep)
        header-spaces (text-table/header-spaces space-columns spaces)
        cells (text-table/merge-cells ifc-column brick-column env-columns profile-columns loc-columns header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace show-loc?]
  (text-table/print-table (table workspace show-loc?)))

(defn print-table-str-keys [workspace show-loc?]
  (print-table (walk/keywordize-keys workspace) show-loc?))
