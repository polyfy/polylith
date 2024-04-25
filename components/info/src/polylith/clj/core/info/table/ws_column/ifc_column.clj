(ns ^:no-doc polylith.clj.core.info.table.ws-column.ifc-column
  (:require [polylith.clj.core.text-table.interface :as text-table]))

(defn ifc-name [component]
  (-> component :interface :name))

(defn ifc-cell [index component]
  (let [name (or (-> component :interface :name) "-")
        row (+ index 3)]
    (text-table/cell 1 row name :yellow)))

(defn ws-ifc-cell [row start-row {:keys [alias interface]}]
  (text-table/cell 1 (+ row start-row) (str alias "/" interface) :yellow))

(defn base-cell [row start-row _]
  (text-table/cell 1 (+ row start-row) "-" :none))

(defn column [components bases ws-components ws-bases]
  (let [ws-component-start-row (+ (count components) 3)
        base-start-row (+ ws-component-start-row (count ws-components))]
    (concat
      [(text-table/cell 1 "interface")]
      (map-indexed ifc-cell components)
      (map-indexed #(ws-ifc-cell %1 ws-component-start-row %2) ws-components)
      (map-indexed #(base-cell %1 base-start-row %2)
                   (concat bases ws-bases)))))
