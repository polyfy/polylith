(ns ^:no-doc polylith.clj.core.info.table.ws-column.ifc-column
  (:require [polylith.clj.core.text-table.interface :as text-table]
            [polylith.clj.core.info.table.ws-column.shared :as shared]))

(defn cell [index {:keys [alias interface]}]
  (text-table/cell 1
                   (+ 3 index)
                   (shared/full-name alias interface)
                   :yellow))

(defn column [bricks]
  (concat
    [(text-table/cell 1 "interface")]
    (map-indexed cell bricks)))
