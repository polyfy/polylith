(ns ^:no-doc polylith.clj.core.lib.used-libs)

(defn symbolize [[k {:keys [type version]}]]
  (if (= "maven" type)
    [(symbol k) version]
    []))

(defn entity-lib [{:keys [name lib-deps]}]
  [name (into {} (concat (map symbolize (-> lib-deps :src))
                         (map symbolize (-> lib-deps :test))))])

(defn name->lib->version [entities]
  (into {} (filter #(-> % second seq)
                   (map entity-lib entities))))

(defn type->name->lib->version [{:keys [bases components projects]}]
  {:base (name->lib->version bases)
   :component (name->lib->version components)
   :project (name->lib->version projects)})
