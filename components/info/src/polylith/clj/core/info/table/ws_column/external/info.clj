(ns ^:no-doc polylith.clj.core.info.table.ws-column.external.info
  (:require [polylith.clj.core.common.interface :as common]))

(defn full-name [alias name]
  (str alias "/" name))

(defn component [{:keys [alias name type interface]} alias->workspace]
  (let [{:keys [components] :as workspace} (alias->workspace alias)
        changed-components (-> workspace :changes :changed-components set)
        {:keys [lines-of-code]} (common/find-component name components)]
    {:alias alias
     :name name
     :type type
     :interface (or interface "-")
     :lines-of-code lines-of-code
     :changed? (contains? changed-components name)}))

(defn base [{:keys [alias name type]} alias->workspace]
  (let [{:keys [bases] :as workspace} (alias->workspace alias)
        changed-bases (-> workspace :changes :changed-bases set)
        {:keys [lines-of-code]} (common/find-base name bases)]
    {:alias alias
     :name name
     :type type
     :interface "-"
     :lines-of-code lines-of-code
     :changed? (contains? changed-bases name)}))
