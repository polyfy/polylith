(ns polylith.clj.core.text-table2.flipper
  (:require [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn select-row [row rows]
  (filterv #(= row (:row %)) rows))

(defn row-height [{:keys [value orientation]}]
  (case orientation
    :horizontal 1
    :vertical (-> value color/clean-colors count)
    (throw (Exception. (str "Illegal orientation: " orientation)))))

(defn max-row-height [row rows]
  (apply max (map row-height (select-row row rows))))

(defn add-next [result row]
  (conj result (+ (last result) row)))

(defn vertical-cell [index char row column align color start-y]
  (let [x column
        y (+ row start-y index)]
    [[x y] {:value (str char)
            :align align
            :color color
            :orientation :vertical}]))

(defn flip-vertical [row column value align color row->start-y row->max-row-height]
  (let [max-row-height (row->max-row-height row)
        spaces (str-util/spaces (- max-row-height (count value)))
        new-value (str spaces value)
        start-y (row->start-y row)]
    (map-indexed #(vertical-cell %1 %2 row column align color start-y)
                 (vec new-value))))

(defn horizontal [row column value align color row->start-y row->max-row-height]
  (let [x column
        y (+ row (row->start-y row) (row->max-row-height row) -1)]
    [[[x y] {:value value
             :align align
             :color color
             :orientation :horizontal}]]))

(defn flip-row [{:keys [row column value align color orientation]} row->start-y row->max-row-height]
  (case orientation
    :horizontal (horizontal row column value align color row->start-y row->max-row-height)
    :vertical (flip-vertical row column value align color row->start-y row->max-row-height)))

(defn flip-to-map [row-cells]
  (let [rows-numbers (sort (set (map :row row-cells)))
        start-ys (rest (reduce add-next [0]
                               (drop-last (conj (map #(max-row-height % row-cells)
                                                     rows-numbers) 0))))
        row->start-y (into {} (map vector rows-numbers start-ys))
        max-row-heights (map #(max-row-height % row-cells) rows-numbers)
        row->max-row-height (into {} (map vector rows-numbers max-row-heights))]
    (into {} (mapcat #(flip-row % row->start-y row->max-row-height) row-cells))))
