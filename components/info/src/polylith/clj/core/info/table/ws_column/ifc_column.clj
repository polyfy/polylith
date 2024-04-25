(ns ^:no-doc polylith.clj.core.info.table.ws-column.ifc-column
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn cell [index {:keys [interface]}]
  (text-table/cell 1 (+ 3 index) interface :yellow))

(defn column [bricks]
  (concat
    [(text-table/cell 1 "interface")]
    (map-indexed cell bricks)))
