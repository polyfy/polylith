(ns polylith.clj.core.deps.text-table.shared
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.text-table.interface :as text-table]))

(def type->color {"component" :green
                  "base" :blue})

(defn brick->color [{:keys [components bases]}]
  (into {} (map (juxt :name #(-> % :type type->color))
                (concat components bases))))

(defn brick-headers [{:keys [name type]} color-mode]
  [(text-table/cell 3 1 "<" :none :left :horizontal)
   (text-table/cell 5 1 (color/brick type name color-mode) :none :left :horizontal)
   (text-table/cell 7 1 ">" :none :left :horizontal)
   (text-table/cell 9 1 "uses" :none :left :horizontal)])

(defn deps-cell [column row [name color]]
  (text-table/cell column row name color :left :horizontal))

(defn deps-column [column header rows]
  (let [cells (concat
                [(text-table/cell column 1 header :none :left :horizontal)]
                (map-indexed #(deps-cell column (+ %1 3) %2)
                             rows))
        line (text-table/line 2 cells)]
    (text-table/merge-cells cells line)))
