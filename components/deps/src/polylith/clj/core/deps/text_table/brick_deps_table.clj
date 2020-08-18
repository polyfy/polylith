(ns polylith.clj.core.deps.text-table.brick-deps-table
  (:require [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.deps.brick-deps :as brick-deps]
            [polylith.clj.core.util.interfc.color :as color]))

(def type->color {"component" :green
                  "base" :blue})

(defn deps-cell [column row [name color]]
  (text-table/cell column row name color :left :horizontal))

(defn deps-column [column header rows]
  (let [cells (concat
                [(text-table/cell column 1 header :none :left :horizontal)]
                (map-indexed #(deps-cell column (+ %1 3) %2)
                             rows))
        line (text-table/line 2 cells)]
    (text-table/merge-cells cells line)))

(defn brick-headers [{:keys [name type]} color-mode]
  [(text-table/cell 3 1 "<" :none :left :horizontal)
   (text-table/cell 5 1 (color/brick type name color-mode) :none :left :horizontal)
   (text-table/cell 7 1 ">" :none :left :horizontal)
   (text-table/cell 9 1 "uses" :none :left :horizontal)])

(defn table [{:keys [components bases settings]} environment brick]
  (let [color-mode (:color-mode settings)
        bricks (concat components bases)
        brick-name (:name brick)
        brick->color (into {} (map (juxt :name #(-> % :type type->color)) bricks))
        brick->interface-deps (into {} (map (juxt :name :interface-deps) bricks))
        {:keys [dependers dependees]} (brick-deps/deps environment brick->color brick->interface-deps brick-name)
        used-by-column (deps-column 1 "used by" dependers)
        uses-column (deps-column 9 "uses" dependees)
        headers (brick-headers brick color-mode)
        spaces (text-table/header-spaces [2 4 6 8] (repeat "  "))]
    (text-table/table "  " color-mode used-by-column uses-column headers spaces)))

(defn validate [environment-name brick-name environment brick color-mode]
  (cond
    (nil? environment) [false (str "  Couldn't find the " (color/environment environment-name color-mode) " environment.")]
    (nil? brick) [false (str "  Couldn't find brick '" brick-name "'.")]
    :else [true]))

(defn print-table [{:keys [environments] :as workspace} environment-name brick-name]
  (let [color-mode (-> workspace :settings :color-mode)
        environment (common/find-environment environment-name environments)
        brick (common/find-brick brick-name workspace)
        [ok? message] (validate environment-name brick-name environment brick color-mode)]
    (if ok?
      (text-table/print-table (table workspace environment brick))
      (println message))))
