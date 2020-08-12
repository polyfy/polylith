(ns polylith.clj.core.text-table2.line
  (:require [polylith.clj.core.util.interfc.str :as str-util]))

(defn select-column [column rows]
  (filterv #(= column (:column %)) rows))

(defn max-column-width [column rows]
  (apply max (map #(-> % :value count)
                  (select-column column rows))))

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
