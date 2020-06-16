(ns polylith.deps.interface-deps)

(defn deps [[interface components]]
  [interface (vec (mapcat :dependencies components))])

(defn merge-interface [result {:keys [name] :as interface}]
  (assoc result name interface))

(defn merge-deps [result [interface deps]]
  (assoc-in result [interface :implementing-deps] deps))

(defn dependencies [interfaces components]
  "Calculates all interface dependencies, which are the set of dependencies for
   all components that implement an interface."

  (let [component-interface-groups (group-by #(-> % :interface :name) components)
        interface-deps (map deps component-interface-groups)
        name->interface (reduce merge-interface {} interfaces)]
    (vec (sort-by :name (map second
                             (reduce merge-deps name->interface interface-deps))))))
