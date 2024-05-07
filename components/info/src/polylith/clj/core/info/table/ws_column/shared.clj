(ns ^:no-doc polylith.clj.core.info.table.ws-column.shared)

(defn full-name [alias name]
  (if (and alias
           (not= "-" name))
    (str alias "/" name)
    name))
