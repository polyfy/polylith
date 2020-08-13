(ns polylith.clj.core.workspace.new-ws-table.core
  (:require [clojure.string :as str]
            [polylith.clj.core.text-table2.interfc :as text-table]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.workspace.new-ws-table.ifc-column :as ifc-column]
            [polylith.clj.core.workspace.new-ws-table.brick-column :as brick-column]
            [polylith.clj.core.workspace.new-ws-table.profile-columns :as profile-columns]
            [polylith.clj.core.workspace.new-ws-table.env-columns :as env-columns]))

(defn component-sorter [{:keys [interface name]}]
  [(:name interface) name])

(defn table [{:keys [settings environments components bases changes]} show-loc?]
  (let [color-mode (:color-mode settings)
        thousand-sep (user-config/thousand-separator)
        profiles (profile-columns/show-profiles settings)
        sorted-components (sort-by component-sorter components)
        bricks (concat sorted-components bases)
        space-columns (range 2 (* 2 (+ 2 (count environments) (count profiles) (if show-loc? 2 0))) 2)
        spaces (concat (repeat (-> space-columns count dec) "  ") [" "])
        profile-start-column (+ 5 (* 2 (count environments)))
        ifc-columns (ifc-column/column sorted-components bases)
        brick-columns (brick-column/column bricks changes color-mode)
        env-columns (env-columns/columns environments bricks changes settings show-loc? thousand-sep color-mode)
        profile-columns (profile-columns/columns profile-start-column bricks profiles settings)
        header-spaces (text-table/header-spaces space-columns spaces)
        cells (text-table/merge-cells ifc-columns brick-columns env-columns profile-columns header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace show-loc?]
  (println (str/join "\n" (table workspace show-loc?))))
