(ns polylith.clj.core.deps.text-table.workspace-brick-deps-table
  (:require [polylith.clj.core.text-table2.interfc :as text-table]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.common.interfc :as common]))

(defn brick-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn brick-column [bricks color-mode]
  (concat
    [(text-table/cell 1 1 "brick" :none :left :horizontal)]
    (map-indexed #(brick-cell (+ %1 3) %2 color-mode)
                 bricks)))

(defn interface-cell [column row component-name brick-name brick->deps brick->indirect-deps]
  (let [value (cond
                (contains? (brick->deps brick-name) component-name) "x"
                (contains? (brick->indirect-deps brick-name) component-name) "+"
                :else "·")]
    (text-table/cell column row value :none :center :horizontal)))

(defn interface-column [column component-name brick-names brick->deps brick->indirect-deps]
  (concat
    [(text-table/cell column 1 component-name :green :left :vertical)]
    (map-indexed #(interface-cell column (+ %1 3) component-name %2 brick->deps brick->indirect-deps)
                 brick-names)))

(defn interface-columns [component-names brick-names brick->deps brick->indirect-deps]
  (apply concat (map-indexed #(interface-column (+ (* %1 2) 3) %2 brick-names brick->deps brick->indirect-deps)
                             component-names)))

(defn table [{:keys [settings components bases]} environment]
  (let [color-mode (:color-mode settings)
        deps (:deps environment)
        bricks (concat components bases)
        brick-names (map :name bricks)
        component-names (map :name components)
        brick->deps (into {} (mapv (juxt identity #(-> % deps :direct set)) brick-names))
        brick->indirect-deps (into {} (mapv (juxt identity #(-> % deps :indirect set)) brick-names))
        space-columns (range 2 (* 2 (inc (count components))) 2)
        spaces (repeat "  ")
        header-spaces (text-table/header-spaces space-columns spaces)
        brick-col (brick-column bricks color-mode)
        component-cols (interface-columns component-names brick-names brick->deps brick->indirect-deps)
        cells (text-table/merge-cells brick-col component-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [{:keys [environments settings] :as workspace} environment-name]
  (if-let [environment (common/find-environment environment-name environments)]
    (text-table/print-table (table workspace environment))
    (println (str "Couldn't find the " (color/environment environment-name (:color-mode settings)) " environment."))))
