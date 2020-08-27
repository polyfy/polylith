(ns polylith.clj.core.text-table.interfc
  (:require [polylith.clj.core.text-table.core :as core]
            [polylith.clj.core.text-table.cell :as cell]
            [polylith.clj.core.text-table.spaces :as spaces]
            [polylith.clj.core.text-table.line :as line]
            [polylith.clj.core.text-table.merger :as merger]))

(defn cell
  ([column value]
   (cell/cell column 1 value :none :left :horizontal))
  ([column row value]
   (cell/cell column row value :none :left :horizontal))
  ([column row value color]
   (cell/cell column row value color :left :horizontal))
  ([column row value color align]
   (cell/cell column row value color align :horizontal))
  ([column row value color align orientation]
   (cell/cell column row value color align orientation)))

(defn number-cell [number column row align thousand-sep]
  (cell/number-cell number column row align thousand-sep))

(defn spaces [row column-nums spaces]
  (spaces/spaces row column-nums spaces))

(defn line [row cells]
  (line/line row cells))

(defn merge-cells [& list-of-cells]
  (merger/merge-list-of-cells list-of-cells))

(defn table [initial-spaces color-mode & cells-list]
  (core/table initial-spaces color-mode cells-list))

(defn print-table [table]
  (core/print-table table))
