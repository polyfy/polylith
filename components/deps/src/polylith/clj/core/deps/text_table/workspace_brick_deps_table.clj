(ns polylith.clj.core.deps.text-table.workspace-brick-deps-table
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn brick-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn brick-column [bricks color-mode]
  (concat
    [(text-table/cell 1 1 "brick" :none :left :horizontal)]
    (map-indexed #(brick-cell (+ %1 3) %2 color-mode)
                 bricks)))

(defn interface-cell [column row {:keys [type name]} brick-name brick->deps brick->indirect-deps brick->ifc-deps empty-character]
  (let [value (cond
                (and (= "component" type) (contains? (brick->deps brick-name) name)) "x"
                (and (= "interface" type) (contains? (brick->ifc-deps brick-name) name)) "x"
                (and (= "component" type) (contains? (brick->indirect-deps brick-name) name)) "+"
                :else empty-character)]
    (text-table/cell column row value :none :center :horizontal)))

(def type->color {"interface" :yellow
                  "component" :green})

(defn interface-column [column {:keys [type name] :as entity} brick-names brick->deps brick->indirect-deps brick->ifc-deps empty-character]
  (concat
    [(text-table/cell column 1 name (type->color type) :right :vertical)]
    (map-indexed #(interface-cell column (+ %1 3) entity %2 brick->deps brick->indirect-deps brick->ifc-deps empty-character)
                 brick-names)))

(defn entity-columns [entities brick-names brick->deps brick->indirect-deps brick->ifc-deps empty-character]
  (apply concat (map-indexed #(interface-column (+ (* %1 2) 3) %2 brick-names brick->deps brick->indirect-deps brick->ifc-deps empty-character)
                             entities)))

(def sorter {"interface" 1
             "component" 2})

(defn entity [name type]
  {:name name
   :type type})

(defn brick-entity [{:keys [direct direct-ifc indirect]}]
  (concat (map #(entity % "interface") direct-ifc)
          (map #(entity % "component") direct)
          (map #(entity % "component") indirect)))

(defn table [{:keys [settings components bases]} project]
  (let [{:keys [color-mode empty-character]} settings
        deps (:deps project)
        brick-names-set (set (map key deps))
        bricks (filter #(contains? brick-names-set (:name %))
                       (concat components bases))
        brick-names (map :name bricks)
        entities (sort-by (juxt #(-> % :type sorter) :name)
                          (set (mapcat brick-entity (map second deps))))
        brick->deps (into {} (mapv (juxt identity #(-> % deps :direct set)) brick-names))
        brick->ifc-deps (into {} (mapv (juxt identity #(-> % deps :direct-ifc set)) brick-names))
        brick->indirect-deps (into {} (mapv (juxt identity #(-> % deps :indirect set)) brick-names))
        space-columns (range 2 (* 2 (inc (count entities))) 2)
        spaces (repeat "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        brick-col (brick-column bricks color-mode)
        entity-cols (entity-columns entities brick-names brick->deps brick->indirect-deps brick->ifc-deps empty-character)
        cells (text-table/merge-cells brick-col entity-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [{:keys [projects settings] :as workspace} project-name]
  (if-let [project (common/find-project project-name projects)]
    (text-table/print-table (table workspace project))
    (println (str "  Couldn't find the " (color/project project-name (:color-mode settings)) " project."))))
