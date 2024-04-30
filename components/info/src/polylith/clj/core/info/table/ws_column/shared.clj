(ns ^:no-doc polylith.clj.core.info.table.ws-column.shared)

(defn full-name [alias name]
  (if alias
    (str alias "/" name)
    name))
