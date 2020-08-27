(ns polylith.clj.core.text-table.cell
  (:require [polylith.clj.core.util.interfc.str :as str-util]))

(defn cell [column row value color align orientation]
  {:column column
   :row row
   :value value
   :color color
   :align align
   :orientation orientation})

(defn number-cell [number column row align thousand-sep]
  (let [value (str-util/sep-1000 number thousand-sep)]
    (cell column row value :none align :horizontal)))
