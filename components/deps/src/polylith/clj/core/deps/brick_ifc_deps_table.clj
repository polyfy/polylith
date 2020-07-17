(ns polylith.clj.core.deps.brick-ifc-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.common.interfc :as common]))

(defn row [interface-name]
  [interface-name])

(def headers ["uses"])
(def alignments [:left])
(def header-colors [:none])

(defn table [{:keys [interface-deps]} color-mode]
  (let [rows (mapv row interface-deps)
        row-colors (repeat (count rows) [:yellow])]
    (text-table/table "  " headers alignments rows header-colors row-colors color-mode)))

(defn print-table [workspace brick-name color-mode]
  (let [brick (common/find-brick workspace brick-name)]
    (if brick
      (println (table brick color-mode))
      (println (str "Couldn't find brick '" brick-name "'.")))))
