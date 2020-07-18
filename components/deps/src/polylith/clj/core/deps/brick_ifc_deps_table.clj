(ns polylith.clj.core.deps.brick-ifc-deps-table
  (:require [polylith.clj.core.text-table.interfc :as text-table]
            [polylith.clj.core.common.interfc :as common]))

(defn row [interface-name]
  [interface-name])

(def headers ["uses"])
(def alignments (repeat [:left]))

(defn table [{:keys [interface-deps]} color-mode]
  (let [interface-rows (mapv row interface-deps)
        rows (concat [headers]
                     [(text-table/full-line (conj interface-rows headers))]
                     interface-rows)
        colors (conj (repeat (count rows) [:yellow]) [:none] [:none])]
    (text-table/table "  " alignments colors rows color-mode)))

(defn print-table [workspace brick-name color-mode]
  (let [brick (common/find-brick workspace brick-name)]
    (if brick
      (println (table brick color-mode))
      (println (str "Couldn't find brick '" brick-name "'.")))))
