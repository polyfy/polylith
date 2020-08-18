(ns polylith.clj.core.text-table.cell)

(defn cell [column row value color align orientation]
  {:column column
   :row row
   :value value
   :color color
   :align align
   :orientation orientation})
