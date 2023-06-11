(ns polylith.clj.core.deps.text-table.workspace-deps-table
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]))

(defn brick-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn brick-column [bricks color-mode]
  (concat
    [(text-table/cell 1 1 "brick" :none :left :horizontal)]
    (map-indexed #(brick-cell (+ %1 3) %2 color-mode)
                 bricks)))

(defn cell [column row interface-name brick-name brick->ifc-deps empty-character]
  (let [value (cond
                (contains? (-> brick-name brick->ifc-deps :src set) interface-name) "x"
                (contains? (-> brick-name brick->ifc-deps :test set) interface-name) "t"
                :else empty-character)]
    (text-table/cell column row value :none :center :horizontal)))

(defn entity-column [column color interface-name brick-names brick->ifc-deps empty-character]
  (concat
    [(text-table/cell column 1 interface-name color :right :vertical)]
    (map-indexed #(cell column (+ %1 3) interface-name %2 brick->ifc-deps empty-character)
                 brick-names)))

(defn columns [column color interface-names brick-names brick->ifc-deps empty-character]
  (apply concat (map-indexed #(entity-column (+ (* %1 2) column) color %2 brick-names brick->ifc-deps empty-character)
                             interface-names)))

(defn table [{:keys [settings components bases] :as workspace}]
  (let [{:keys [color-mode empty-character]} settings
        bricks (concat components bases)
        brick->ifc-deps (into {} (concat (map (juxt :name :interface-deps) bricks)))
        brick->base-deps (into {} (map (juxt :name :base-deps) bases))
        used-interfaces (sort (set (mapcat #(concat (-> % :interface-deps :src)
                                                    (-> % :interface-deps :test)) components)))
        used-bases (sort (set (mapcat #(concat (-> % :base-deps :src)
                                               (-> % :base-deps :test)) bases)))
        base-col (+ 3 (* 2 (count used-interfaces)))
        brick-names (map :name bricks)
        space-columns (range 2 (* 2 (+ 1 (count used-interfaces) (count used-bases))) 2)
        compact? (common/compact? workspace "deps")
        spaces (conj (repeat (if compact? " " "  ")) "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        brick-col (brick-column bricks color-mode)
        interface-cols (columns 3 :yellow used-interfaces brick-names brick->ifc-deps empty-character)
        base-cols (columns base-col :blue used-bases brick-names brick->base-deps empty-character)
        cells (text-table/merge-cells brick-col interface-cols base-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace]
  (text-table/print-table (table workspace)))

(comment
  (require '[dev.jocke :as dev])
  (def workspace dev/workspace)
  (print-table workspace)
  #__)
