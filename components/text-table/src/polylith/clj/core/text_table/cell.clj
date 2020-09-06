(ns polylith.clj.core.text-table.cell
  (:require [polylith.clj.core.util.interface.str :as str-util]))

(defn cell [column row value color align orientation]
  {:column column
   :row row
   :value value
   :color color
   :align align
   :orientation orientation})

(defn number-cell [column row number align thousand-sep]
  (let [value (str-util/sep-1000 number thousand-sep)]
    (cell column row value :none align :horizontal)))
