(ns polylith.clj.core.deps.text-table.brick-ifc-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.common.interfc :as common]))

(defn row [interface-name]
  [interface-name])

(def headers ["uses"])
(def alignments [:left])
(def header-orientations [:horizontal])

(defn table [{:keys [interface-deps]} color-mode]
  (let [header-colors [:none]
        row-colors (repeat [:yellow])
        rows (mapv row interface-deps)]
    (text-table/table "  " alignments header-colors header-orientations row-colors headers rows color-mode)))

(defn print-table [workspace brick-name color-mode]
  (let [brick (common/find-brick brick-name workspace)]
    (if brick
      (println (table brick color-mode))
      (println (str "Couldn't find brick '" brick-name "'.")))))
