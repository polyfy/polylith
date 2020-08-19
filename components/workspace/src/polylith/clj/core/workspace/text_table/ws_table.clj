(ns polylith.clj.core.workspace.text-table.ws-table
  (:require [clojure.walk :as walk]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.workspace.text-table.ws-table-column.ifc-column :as ifc-column]
            [polylith.clj.core.workspace.text-table.ws-table-column.brick-column :as brick-column]
            [polylith.clj.core.workspace.text-table.ws-table-column.loc-columns :as loc-columns]
            [polylith.clj.core.workspace.text-table.ws-table-column.profile-columns :as profile-columns]
            [polylith.clj.core.workspace.text-table.ws-table-column.env-columns :as env-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn profile-sorting [profile]
  [(not= "default" profile) profile])

(defn profiles-to-show [{:keys [profile->settings active-dev-profiles]}]
  (sort-by profile-sorting
           (filter #(not (contains? active-dev-profiles %))
                   (map first profile->settings))))

(defn table [{:keys [ws-dir settings environments components bases changes]} show-loc? show-resources?]
  (let [{:keys [color-mode thousand-sep]} settings
        profiles (profiles-to-show settings)
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        space-columns (range 2 (* 2 (+ 2 (count environments) (count profiles) (if show-loc? 2 0))) 2)
        spaces (concat (repeat (-> space-columns count dec) "  ") (if show-loc? [" "] ["  "]))
        profile-start-column (+ 5 (* 2 (count environments)))
        loc-start-column (+ profile-start-column (* 2 (count profiles)))
        ifc-column (ifc-column/column sorted-components bases)
        brick-column (brick-column/column bricks changes color-mode)
        env-columns (env-columns/columns ws-dir environments bricks changes show-loc? show-resources? thousand-sep)
        profile-columns (profile-columns/columns ws-dir profile-start-column bricks profiles settings show-resources?)
        loc-columns (loc-columns/columns show-loc? bricks loc-start-column thousand-sep)
        header-spaces (text-table/spaces 1 space-columns spaces)
        cells (text-table/merge-cells ifc-column brick-column env-columns profile-columns loc-columns header-spaces)
        line (text-table/line 2 cells)
        section1 (if (= 1 (count environments)) [] [(* 2 (-> environments count inc))])
        section2 (if show-loc? [(- (last space-columns) 2)] [])
        line-space (text-table/spaces 2 (concat [4] section1 section2) (repeat "   "))]
    (text-table/table "  " color-mode cells line line-space)))

(defn print-table [workspace show-loc? show-resources?]
  (text-table/print-table (table workspace show-loc? show-resources?)))

(defn print-table-str-keys [workspace show-loc? show-resources?]
  (print-table (walk/keywordize-keys workspace) show-loc? show-resources?))
