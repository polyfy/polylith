(ns polylith.clj.core.deps.text-table.project-deps-table
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

(defn interface-cell [column row {:keys [type name]} brick-name deps empty-character]
  (let [value (cond
                (and (= "component" type) (contains? (-> brick-name deps :src :direct set) name)) "x"
                (and (= "component" type) (contains? (-> brick-name deps :test :direct set) name)) "t"
                (and (= "component" type) (contains? (-> brick-name deps :src :indirect set) name)) "+"
                (and (= "component" type) (contains? (-> brick-name deps :test :indirect set) name)) "-"
                (and (= "interface" type) (contains? (-> brick-name deps :src :missing-ifc :direct set) name)) "x"
                (and (= "interface" type) (contains? (-> brick-name deps :test :missing-ifc :direct set) name)) "t"
                (and (= "interface" type) (contains? (-> brick-name deps :src :missing-ifc :indirect set) name)) "+"
                (and (= "interface" type) (contains? (-> brick-name deps :test :missing-ifc :indirect set) name)) "-"
                :else empty-character)]
    (text-table/cell column row value :none :center :horizontal)))

(def type->color {"interface" :yellow
                  "component" :green})

(defn interface-column [column {:keys [type name] :as entity} brick-names deps empty-character]
  (concat
    [(text-table/cell column 1 name (type->color type) :right :vertical)]
    (map-indexed #(interface-cell column (+ %1 3) entity %2 deps empty-character)
                 brick-names)))

(defn entity-columns [entities brick-names deps empty-character]
  (apply concat (map-indexed #(interface-column (+ (* %1 2) 3) %2 brick-names deps empty-character)
                             entities)))

(def sorter {"interface" 1
             "component" 2})

(defn entity [name type]
  {:name name
   :type type})

(defn source-brick-entity [{:keys [direct missing-ifc]}]
  (concat (map #(entity % "interface")
               (:direct missing-ifc))
          (map #(entity % "component")
               direct)))

(defn brick-entity-from-deps [[_ {:keys [src test]}]]
  (concat (source-brick-entity src)
          (source-brick-entity test)))

(defn brick-entity [{:keys [name type]}]
  {:name name
   :type type})

(defn table [{:keys [settings components bases] :as workspace}
             {:keys [deps component-names base-names]} is-all]
  (let [{:keys [color-mode empty-character]} settings
        entity-names (if is-all
                       (set (map key deps))
                       (set (concat (:src component-names)
                                    (:test component-names)
                                    (:src base-names)
                                    (:test base-names))))
        bricks (filter #(contains? entity-names (:name %))
                       (concat components bases))
        brick-names (map :name bricks)
        entities (sort-by (juxt #(-> % :type sorter) :name)
                          (set (cond-> (mapcat brick-entity-from-deps deps)
                                       is-all (concat (map brick-entity components)))))
        compact? (common/compact? workspace "deps")
        space-columns (range 2 (* 2 (inc (count entities))) 2)
        spaces (conj (repeat (if compact? " " "  ")) "  ")
        header-spaces (text-table/spaces 1 space-columns spaces)
        brick-col (brick-column bricks color-mode)
        entity-cols (entity-columns entities brick-names deps empty-character)
        cells (text-table/merge-cells brick-col entity-cols header-spaces)
        line (text-table/line 2 cells)]
    (text-table/table "  " color-mode cells line)))

(defn print-table [{:keys [projects settings] :as workspace} project-name is-all]
  (if-let [project (common/find-project project-name projects)]
    (text-table/print-table (table workspace project is-all))
    (println (str "  Couldn't find the " (color/project project-name (:color-mode settings)) " project."))))

(comment
  (require '[dev.jocke :as dev])
  (def workspace dev/workspace)
  (print-table workspace "inv" false)
  #__)
