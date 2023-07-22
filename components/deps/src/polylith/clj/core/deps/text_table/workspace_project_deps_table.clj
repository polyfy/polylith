(ns polylith.clj.core.deps.text-table.workspace-project-deps-table
  (:require [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn first-cell [row {:keys [name type]} color-mode]
  (text-table/cell 1 row (color/brick type name color-mode) :none :left :horizontal))

(defn first-column [bricks color-mode]
  (concat
    [(text-table/cell 1 1 "brick" :none :left :horizontal)]
    (map-indexed #(first-cell (+ %1 3) %2 color-mode)
                 bricks)))

(defn brick? [type]
  (contains? #{"base" "component"} type))

(defn brick-cell [column row {:keys [type name]} brick-name deps empty-character]
  (let [value (cond
                (and (brick? type) (contains? (-> brick-name deps :src :direct set) name)) "x"
                (and (brick? type) (contains? (-> brick-name deps :test :direct set) name)) "t"
                (and (brick? type) (contains? (-> brick-name deps :src :indirect set) name)) "+"
                (and (brick? type) (contains? (-> brick-name deps :test :indirect set) name)) "-"
                (and (= "interface" type) (contains? (-> brick-name deps :src :missing-ifc-and-bases :direct set) name)) "x"
                (and (= "interface" type) (contains? (-> brick-name deps :test :missing-ifc-and-bases :direct set) name)) "t"
                (and (= "interface" type) (contains? (-> brick-name deps :src :missing-ifc-and-bases :indirect set) name)) "+"
                (and (= "interface" type) (contains? (-> brick-name deps :test :missing-ifc-and-bases :indirect set) name)) "-"
                :else empty-character)]
    (text-table/cell column row value :none :center :horizontal)))

(def type->color {"interface" :yellow
                  "component" :green
                  "base" :blue})

(defn brick-column [column {:keys [type name] :as entity} brick-names deps empty-character]
  (concat
    [(text-table/cell column 1 name (type->color type) :right :vertical)]
    (map-indexed #(brick-cell column (+ %1 3) entity %2 deps empty-character)
                 brick-names)))

(defn brick-columns [entities brick-names deps empty-character]
  (apply concat (map-indexed #(brick-column (+ (* %1 2) 3) %2 brick-names deps empty-character)
                             entities)))

(def sorter {"interface" 1
             "component" 2
             "base" 3})

(defn entity [name type]
  {:name name
   :type type})

(defn brick-type [brick-name all-base-names]
  (if (contains? all-base-names brick-name)
    "base"
    "component"))

(defn source-brick-entity [{:keys [direct missing-ifc-and-bases]} all-base-names]
  (concat (map #(entity % "interface")
               (:direct missing-ifc-and-bases))
          (map #(entity % (brick-type % all-base-names))
               direct)))

(defn brick-entity-from-deps [[_ {:keys [src test]}] all-base-names]
  (concat (source-brick-entity src all-base-names)
          (source-brick-entity test all-base-names)))

(defn table [{:keys [settings components bases] :as workspace}
             {:keys [deps component-names base-names]}]
  (let [{:keys [color-mode empty-character]} settings
        entity-names (set (concat (:src component-names)
                                  (:test component-names)
                                  (:src base-names)
                                  (:test base-names)))
        bricks (filter #(contains? entity-names (:name %))
                       (concat components bases))
        brick-names (map :name bricks)
        all-base-names (set (map :name bases))
        entities (sort-by (juxt #(-> % :type sorter) :name)
                          (set (mapcat #(brick-entity-from-deps % all-base-names) deps)))
        compact? (common/compact? workspace "deps")
        space-columns (range 2 (* 2 (inc (count entities))) 2)
        spaces (conj (repeat (if compact? " " "  ")) "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        first-col (first-column bricks color-mode)
        brick-cols (brick-columns entities brick-names deps empty-character)
        cells (text-table/merge-cells first-col brick-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [{:keys [projects settings] :as workspace} project-name]
  (if-let [project (common/find-project project-name projects)]
    (common/print-or-save-table workspace
                                #(table % project))
    (println (str "  Couldn't find the " (color/project project-name (:color-mode settings)) " project."))))

(comment
  (require '[dev.jocke :as dev])
  (def workspace dev/workspace)
  (print-table workspace "poly")
  #__)
