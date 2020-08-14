(ns polylith.clj.core.workspace.ws-table.shared
  (:require [polylith.clj.core.util.interfc.str :as str-util]
            [polylith.clj.core.text-table2.interfc :as text-table]))

(defn header
  ([value column]
   (header value column :none :left))
  ([value column color align]
   (text-table/cell column 1 value color align :horizontal)))

(defn standard-cell
  ([value column row]
   (standard-cell value column row :none))
  ([value column row color]
   (text-table/cell column row value color :left :horizontal))
  ([value column row color align]
   (text-table/cell column row value color align :horizontal)))

(defn number-cell
  ([number column row align thousand-sep]
   (let [value (str-util/sep-1000 number thousand-sep)]
     (standard-cell value column row :none align))))
