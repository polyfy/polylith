(ns polylith.clj.core.text-table2.merger)

(defn key-row [{:keys [row column value align color orientation]}]
  [[row column] {:row row
                 :column column
                 :value value
                 :align align
                 :color color
                 :orientation orientation}])

(defn as-map [row-cells]
  (into {} (map key-row row-cells)))

(defn merge-two-cells [cells merging-cells]
  (mapv second (merge (as-map cells)
                      (as-map merging-cells))))

(defn merge-list-of-cells [cell-rows-list]
  (reduce #(merge-two-cells %1 %2) cell-rows-list))
