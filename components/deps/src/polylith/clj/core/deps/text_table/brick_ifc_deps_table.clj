(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table
  (:require [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.text-table2.interfc :as text-table]))

(defn interface-cell [row interface-name]
  (text-table/cell 1 row interface-name :yellow :left :horizontal))

(defn interface-column [interface-names]
  (concat
    [(text-table/cell 1 1 "uses" :none :left :horizontal)]
    (map-indexed #(interface-cell (+ %1 3) %2)
                 interface-names)))

(defn table [{:keys [interface-deps]} color-mode]
  (let [interface-col (interface-column interface-deps)
        line (text-table/line 2 interface-col)]
    (text-table/table "  " color-mode interface-col line)))

(defn print-table [workspace brick-name]
  (let [color-mode (-> workspace :settings :color-mode)
        brick (common/find-brick brick-name workspace)]
    (if brick
      (text-table/print-table (table brick color-mode))
      (println (str "Couldn't find brick '" brick-name "'.")))))
