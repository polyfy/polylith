(ns ^:no-doc polylith.clj.core.text-table.cell
  (:require [polylith.clj.core.util.interface.str :as str-util]))

(defn cell [column row value color align orientation]
  {:column column
   :row row
   :value value
   :color color
   :align align
   :orientation orientation})

(defn number-cell [column row number align thousand-separator]
  (let [value (str-util/sep-1000 number thousand-separator)]
    (cell column row value :none align :horizontal)))
