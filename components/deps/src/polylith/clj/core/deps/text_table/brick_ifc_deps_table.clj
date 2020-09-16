(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table
  (:require [polylith.clj.core.deps.text-table.shared :as shared]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.text-table.interface :as text-table]))

(defn interface-cell [row interface-name]
  (text-table/cell 1 row interface-name :yellow :left :horizontal))

(defn interface-column [interface-names]
  (concat
    [(text-table/cell 1 1 "uses" :none :left :horizontal)]
    (map-indexed #(interface-cell (+ %1 3) %2)
                 interface-names)))

(defn used-by-interface [{:keys [name type interface-deps]} interface-name]
  (when (-> (filter #(= interface-name %) interface-deps)
            empty? not)
    [[name (shared/type->color type)]]))

(defn table [{:keys [components bases settings]} brick]
  (let [color-mode (:color-mode settings)
        brick-name (:name brick)
        bricks (concat components bases)
        uses (mapv #(vector % :yellow) (:interface-deps brick))
        uses-column (shared/deps-column 9 "uses" uses)
        used-by (mapcat #(used-by-interface % brick-name) bricks)
        used-by-column (shared/deps-column 1 "used by" used-by)
        headers (shared/brick-headers brick color-mode)
        spaces (text-table/spaces 1 [2 4 6 8] (repeat "  "))]
    (text-table/table "  " color-mode used-by-column uses-column headers spaces)))

(defn print-table [workspace brick-name]
  (let [brick (common/find-brick brick-name workspace)]
    (if brick
      (text-table/print-table (table workspace brick))
      (println (str "  Couldn't find brick '" brick-name "'.")))))
