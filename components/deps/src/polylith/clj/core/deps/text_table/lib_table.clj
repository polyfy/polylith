(ns polylith.clj.core.deps.text-table.lib-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]))

(def type->color {"component" :green
                  "base" :blue})

(defn library-cell [row lib-name]
  (text-table/cell 1 row lib-name :none :left :horizontal))

(defn library-col [lib-names]
  (concat [(text-table/cell 1 1 "library" :none :left :horizontal)]
          (map-indexed #(library-cell (+ 3 %1) %2)
                       lib-names)))

(defn brick-cell [column row lib-name lib-names]
  (let [flag (if (contains? (set lib-names) lib-name) "x" "-")]
    (text-table/cell column row flag :none :left :vertical)))

(defn brick-column [column {:keys [name type lib-dep-names]} lib-names]
  (concat [(text-table/cell column 1 name (type->color type) :right :vertical)]
          (map-indexed #(brick-cell column (+ 3 %1) %2 lib-dep-names)
                       lib-names)))

(defn brick-columns [bricks lib-names]
  (apply concat (map-indexed #(brick-column (+ 3 (* 2 %1)) %2 lib-names)
                             bricks)))

(defn table [{:keys [components bases settings]}]
  (let [color-mode (:color-mode settings)
        bricks (concat components bases)
        lib-names (sort (set (mapcat :lib-dep-names bricks)))
        lib-col (library-col lib-names)
        brick-cols (brick-columns bricks lib-names)
        space-columns (range 2 (* 2 (+ 1 (count bricks))) 2)
        spaces (text-table/spaces 1 space-columns (repeat "  "))
        cells (text-table/merge-cells lib-col brick-cols spaces)
        line (text-table/line 2 cells)]
     (text-table/table "  " color-mode cells line)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
