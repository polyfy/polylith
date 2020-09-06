(ns polylith.clj.core.text-table.spaces)

(defn row-cell [row column value]
  {:row row
   :column column
   :value value
   :align :left
   :color :none
   :orientation :horizontal})

(defn spaces [row column-nums spaces]
  (mapv #(row-cell row %1 %2)
        column-nums
        spaces))
