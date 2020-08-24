(ns polylith.clj.core.workspace.text-table.lib-table-flipped
  (:require [polylith.clj.core.workspace.text-table.shared :as shared]
            [polylith.clj.core.text-table.interfc :as text-table]))

(def type->color {"component" :green
                  "base" :blue})

(defn brick-cell [row {:keys [name type]}]
  (text-table/cell 1 row name (type->color type) :left :horizontal))

(defn bricks-column [bricks]
  (concat [(shared/standard-cell "bricks" 1 1)]
          (map-indexed #(brick-cell (+ 3 %1) %2)
                       bricks)))

(defn lib-name [[name {:keys [mvn/version]}]]
  (when version [name]))

(defn profile-lib-names [[_ {:keys [lib-deps]}]]
  (mapcat lib-name lib-deps))

(defn library-cell [column row lib-name {:keys [lib-dep-names]}]

  (let [flag (if (contains? (set lib-dep-names) lib-name) "x" "-")]
    (text-table/cell column row flag :none :left :vertical)))

(defn library-column [column lib-name bricks]
  (concat [(text-table/cell column 1 lib-name :none :left :vertical)]
          (map-indexed #(library-cell column (+ 3 %1) lib-name %2)
                       bricks)))

(defn library-columns [bricks lib-names]
  (apply concat (map-indexed #(library-column (+ 3 (* 2 %1)) %2 bricks)
                             lib-names)))

(defn table [{:keys [components bases settings]}]
  (let [color-mode (:color-mode settings)
        bricks (concat components bases)
        lib-names (sort (set (mapcat :lib-dep-names bricks)))
        bricks-col (bricks-column bricks)
        library-cols (library-columns bricks lib-names)
        space-columns (range 2 (* 2 (+ 1 (count lib-names))) 2)
        spaces (text-table/spaces 1 space-columns (repeat "  "))
        cells (text-table/merge-cells bricks-col library-cols spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))
