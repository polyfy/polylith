(ns ^:no-doc polylith.clj.core.text-table.line
  (:require [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn select-column [column rows]
  (filterv #(= column (:column %)) rows))

(defn width [{:keys [value orientation]}]
  (if (= :vertical orientation)
    1
    (-> value color/clean-colors count)))

(defn max-column-width [column rows]
  (apply max (map width (select-column column rows))))

(defn line-cell [column row rows]
  (let [max-width (max-column-width column rows)]
    {:column column
     :row row
     :value (str-util/line max-width)
     :color :none
     :align :left
     :orientation :horizontal}))

(defn line [row cells]
  (mapv #(line-cell % row cells)
        (sort (set (map :column cells)))))

(defn line-spaces [row cells]
  (mapv #(line-cell % row cells)
        (sort (set (map :column cells)))))
