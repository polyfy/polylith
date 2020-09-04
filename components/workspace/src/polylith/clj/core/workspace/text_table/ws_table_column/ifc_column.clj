(ns polylith.clj.core.workspace.text-table.ws-table-column.ifc-column
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn ifc-name [component]
  (-> component :interface :name))

(defn ifc-cell [index component]
  (let [name (-> component :interface :name)
        row (+ index 3)]
    (text-table/cell 1 row name :yellow)))

(defn base-cell [row start-row _]
  (text-table/cell 1 (+ row start-row) "-" :none))

(defn column [components bases]
  (let [base-start-row (+ (count components) 3)]
    (concat
      [(text-table/cell 1 "interface")]
      (map-indexed ifc-cell components)
      (map-indexed #(base-cell %1 base-start-row %2) bases))))
