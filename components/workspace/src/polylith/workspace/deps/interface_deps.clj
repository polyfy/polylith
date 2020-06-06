(ns polylith.workspace.deps.interface-deps)

(defn deps [[interface components]]
  [interface (vec (mapcat :dependencies components))])

(defn merge-interface [result {:keys [name] :as interface}]
  (assoc result name interface))

(defn merge-deps [result [interface deps]]
  (assoc-in result [interface :dependencies] deps))

(defn with-deps [interfaces components]
  (let [component-interface-groups (group-by #(-> % :interface :name) components)
        interface-deps (map deps component-interface-groups)
        name->interface (reduce merge-interface {} interfaces)]
    (mapv second
          (reduce merge-deps name->interface interface-deps))))
