(ns polylith.clj.core.workspace.text-table.ws-table-column.ifc-column
  (:require [polylith.clj.core.workspace.text-table.shared :as shared]))

(defn ifc-name [component]
  (-> component :interface :name))

(defn ifc-cell [index component]
  (let [name (-> component :interface :name)
        row (+ index 3)]
    (shared/standard-cell name 1 row :yellow)))

(defn base-cell [row start-row _]
  (shared/standard-cell "-" 1 (+ row start-row) :none))

(defn column [components bases]
  (let [base-start-row (+ (count components) 3)]
    (concat
      [(shared/header "interface" 1)]
      (map-indexed ifc-cell components)
      (map-indexed #(base-cell %1 base-start-row %2) bases))))
