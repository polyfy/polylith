(ns ^:no-doc polylith.clj.core.deps.text-table.workspace-deps-table
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.util.interface.color :as color]))

(def type->color {"base" :blue
                  "component" :green})

(defn interface-cell [row entity-type entity-name color-mode]
  (text-table/cell 1 row (color/entity entity-type entity-name color-mode) :none :left :horizontal))

(defn interface-column [interfaces base-names color-mode]
  (concat
    [(text-table/cell 1 1
                      (if (empty? base-names)
                        "interface"
                        "interface/base")
                      :none :left :horizontal)]
    (map-indexed #(interface-cell (+ %1 3) "interface" %2 color-mode)
                 interfaces)
    (map-indexed #(interface-cell (+ %1 3 (count interfaces)) "base" %2 color-mode)
                 base-names)))

(defn brick-column [brick-names color-mode]
  (map-indexed #(interface-cell (+ %1 3) "base" %2 color-mode)
               brick-names))

(defn brick-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn first-column [bricks color-mode]
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

(defn ifc-columns [column color interface-names brick-names brick->ifc-deps empty-character]
  (apply concat (map-indexed #(entity-column (+ (* %1 2) column) color %2 brick-names brick->ifc-deps empty-character)
                             interface-names)))

(defn brick-column [column row {:keys [type name]} interface-names brick->ifc-deps empty-character]
  (concat
    [(text-table/cell column 1 name
                      (type->color type)
                      :right :vertical)]
    (map-indexed #(cell column (+ %1 2 row) %2 name brick->ifc-deps empty-character)
                 interface-names)))

(defn brick-columns [column row interface-names bricks brick->ifc-deps empty-character]
  (apply concat (map-indexed #(brick-column (+ (* %1 2) column) row %2 interface-names brick->ifc-deps empty-character)
                             bricks)))

(defn used-interfaces [bricks]
  (sort (set (mapcat #(concat (-> % :interface-deps :src)
                              (-> % :interface-deps :test)) bricks))))

(defn used-bases [bases]
  (sort (set (mapcat #(concat (-> % :base-deps :src)
                              (-> % :base-deps :test)) bases))))

(defn table [{:keys [settings components bases] :as workspace}]
  (let [{:keys [color-mode empty-character]} settings
        bricks (concat components bases)
        brick->ifc-deps (into {} (map (juxt :name :interface-deps) bricks))
        base->base-deps (into {} (map (juxt :name :base-deps) bases))
        used-interfaces (used-interfaces bricks)
        used-bases (used-bases bases)
        base-col (+ 3 (* 2 (count used-interfaces)))
        brick-names (map :name bricks)
        space-columns (range 2 (* 2 (+ 1 (count used-interfaces) (count used-bases))) 2)
        compact? (common/compact? workspace "deps")
        spaces (conj (repeat (if compact? " " "  ")) "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        brick-col (first-column bricks color-mode)
        interface-cols (ifc-columns 3 :yellow used-interfaces brick-names brick->ifc-deps empty-character)
        base-cols (ifc-columns base-col :blue used-bases brick-names base->base-deps empty-character)
        cells (text-table/merge-cells brick-col interface-cols base-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn swapped-table [{:keys [settings components bases] :as workspace}]
  (let [{:keys [color-mode empty-character]} settings
        bricks (concat components bases)
        brick->ifc-deps (into {} (map (juxt :name :interface-deps) bricks))
        base->base-deps (into {} (map (juxt :name :base-deps) bases))
        used-interfaces (used-interfaces bricks)
        used-bases (sort (set (mapcat #(concat (-> % :base-deps :src)
                                               (-> % :base-deps :test)) bases)))
        space-columns (range 2 (* 2 (+ 1 (count bricks))) 2)
        compact? (common/compact? workspace "deps")
        spaces (conj (repeat (if compact? " " "  ")) "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        interface-col (interface-column used-interfaces used-bases color-mode)
        base-row (inc (count used-interfaces))
        brick-cols (brick-columns 3 1 used-interfaces bricks brick->ifc-deps empty-character)
        brick-base-cols (brick-columns 3 base-row used-bases bricks base->base-deps empty-character)
        cells (text-table/merge-cells interface-col brick-cols brick-base-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [workspace]
  (if (-> workspace :user-input :is-swap-axes)
    (common/print-or-save-table workspace swapped-table)
    (common/print-or-save-table workspace table)))

(comment
  (def workspace dev.jocke/workspace)
  (print-table workspace)
  (print-table (assoc-in workspace [:user-input :is-swap-axes] true))
  #__)
