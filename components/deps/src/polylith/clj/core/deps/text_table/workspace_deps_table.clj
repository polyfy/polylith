(ns polylith.clj.core.deps.text-table.workspace-deps-table
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]))

(defn brick-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn brick-column [bricks color-mode]
  (concat
    [(text-table/cell 1 1 "brick" :none :left :horizontal)]
    (map-indexed #(brick-cell (+ %1 3) %2 color-mode)
                 bricks)))

(defn interface-cell [column row interface-name brick-name brick->ifc-deps empty-character]
  (let [value (cond
                (contains? (-> brick-name brick->ifc-deps :src set) interface-name) "x"
                (contains? (-> brick-name brick->ifc-deps :test set) interface-name) "t"
                :else empty-character)]
    (text-table/cell column row value :none :center :horizontal)))

(defn interface-column [column interface-name brick-names brick->ifc-deps empty-character]
  (concat
    [(text-table/cell column 1 interface-name :yellow :right :vertical)]
    (map-indexed #(interface-cell column (+ %1 3) interface-name %2 brick->ifc-deps empty-character)
                 brick-names)))

(defn interface-columns [interface-names brick-names brick->ifc-deps empty-character]
  (apply concat (map-indexed #(interface-column (+ (* %1 2) 3) %2 brick-names brick->ifc-deps empty-character)
                             interface-names)))

(defn table [{:keys [settings interfaces components bases]}]
  (let [{:keys [color-mode empty-character]} settings
        bricks (concat components bases)
        brick->ifc-deps (into {} (map (juxt :name :interface-deps) bricks))
        interface-names (sort (map :name interfaces))
        brick-names (map :name bricks)
        space-columns (range 2 (* 2 (inc (count interfaces))) 2)
        spaces (repeat "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        brick-col (brick-column bricks color-mode)
        interface-cols (interface-columns interface-names brick-names brick->ifc-deps empty-character)
        cells (text-table/merge-cells brick-col interface-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
