(ns ^:no-doc polylith.clj.core.info.table.ws-column.external.info)

(defn full-name [alias name]
  (str alias "/" name))

(defn component [{:keys [alias name type interface]} alias->workspace]
  (let [changed-components (set (-> alias alias->workspace :changes :changed-components))]
    {:alias alias
     :name name
     :type type
     :interface (or interface "-")
     :changed? (contains? changed-components name)}))

(defn base [{:keys [alias name type interface]} alias->workspace]
  (let [changed-bases (set (-> alias alias->workspace :changes :changed-bases))]
    {:alias alias
     :name name
     :type type
     :interface (or interface "-")
     :changed? (contains? changed-bases name)}))
