(ns polylith.clj.core.text-table2.spaces)

(defn row-cell [row column value]
  {:row row
   :column column
   :value value
   :align :left
   :color :none
   :orientation :horizontal})

(defn header-spaces [column-nums spaces]
  (mapv #(row-cell 1 %1 %2)
        column-nums
        spaces))
