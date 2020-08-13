(ns polylith.clj.core.text-table2.interfc
  (:require [polylith.clj.core.text-table2.core :as core]
            [polylith.clj.core.text-table2.cell :as cell]
            [polylith.clj.core.text-table2.spaces :as spaces]
            [polylith.clj.core.text-table2.line :as line]
            [polylith.clj.core.text-table2.merger :as merger]))

(defn cell [column row value color align orientation]
  (cell/cell column row value color align orientation))

(defn header-spaces [column-nums spaces]
  (spaces/header-spaces column-nums spaces))

(defn line [row cells]
  (line/line row cells))

(defn merge-cells [& list-of-cells]
  (merger/merge-list-of-cells list-of-cells))

(defn table [initial-spaces color-mode & cells-list]
  (core/table initial-spaces color-mode cells-list))

(defn print-table [initial-spaces color-mode & cells-list]
  (core/print-table initial-spaces color-mode cells-list))

;(def row-cells [{:row 1, :column 1, :value "interface", :align :left, :color :none, :orientation :horizontal}
;                {:row 1, :column 3, :value "brick",     :align :left, :color :none, :orientation :horizontal}
;                {:row 3, :column 1, :value "command",   :align :center, :color :yellow, :orientation :vertical}
;                {:row 3, :column 3, :value "command",   :align :left, :color :green, :orientation :horizontal}
;                {:row 4, :column 1, :value "user",      :align :left, :color :yellow, :orientation :horizontal}
;                {:row 4, :column 3, :value "admin",     :align :left, :color :green, :orientation :horizontal}])
;
;(def columns (column/column 2 "  " row-cells))
;(def lines (line/line 2 (merger/merge-cells row-cells columns)))
;
;;(def cells (flipper/flip-to-map (merger/merge-list-of-cells [row-cells columns lines])))
;
;(merger/merge-list-of-cells [row-cells columns lines])
;
;(print-table "  " "dark" row-cells columns lines)
;(print-table "  " "dark" row-cells lines columns)
