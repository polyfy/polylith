(ns polylith.clj.core.deps.text-table.workspace-ifc-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]))

(defn brick-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn brick-column [bricks color-mode]
  (concat
    [(text-table/cell 1 1 "brick" :none :left :horizontal)]
    (map-indexed #(brick-cell (+ %1 3) %2 color-mode)
                 bricks)))

(defn interface-cell [column row interface-name brick-name brick->interface-deps empty-char]
  (let [value (if (contains? (brick->interface-deps brick-name) interface-name)
                "x"
                empty-char)]
    (text-table/cell column row value :none :center :horizontal)))

(defn interface-column [column interface-name brick-names brick->interface-deps empty-char]
  (concat
    [(text-table/cell column 1 interface-name :yellow :right :vertical)]
    (map-indexed #(interface-cell column (+ %1 3) interface-name %2 brick->interface-deps empty-char)
                 brick-names)))

(defn interface-columns [interface-names brick-names brick->interface-deps empty-char]
  (apply concat (map-indexed #(interface-column (+ (* %1 2) 3) %2 brick-names brick->interface-deps empty-char)
                             interface-names)))

(defn table [{:keys [settings interfaces components bases]}]
  (let [{:keys [color-mode empty-char]} settings
        bricks (concat components bases)
        brick->interface-deps (into {} (map (juxt :name #(-> % :interface-deps set)) bricks))
        interface-names (sort (map :name interfaces))
        brick-names (map :name bricks)
        space-columns (range 2 (* 2 (inc (count interfaces))) 2)
        spaces (repeat "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        brick-col (brick-column bricks color-mode)
        interface-cols (interface-columns interface-names brick-names brick->interface-deps empty-char)
        cells (text-table/merge-cells brick-col interface-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
