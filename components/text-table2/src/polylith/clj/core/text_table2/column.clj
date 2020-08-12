(ns polylith.clj.core.text-table2.column)

(defn row-cell [row column value]
  {:row row
   :column column
   :value value
   :align :left
   :color :none
   :orientation :horizontal})

(defn column [col value row-cells]
  (mapv #(row-cell % col value)
        (sort (set (map :row row-cells)))))
