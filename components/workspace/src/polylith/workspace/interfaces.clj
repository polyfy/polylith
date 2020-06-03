(ns polylith.workspace.interfaces)

(defn ->interface [[_ [{:keys [name interface]}]]]
  (assoc interface :implemented-by [name]))

(defn ->multi-interface [[interface-name components]]
  {:type "interface"
   :name interface-name
   :declarations (vec (sort-by (juxt :type :name :signature)
                               (set (mapcat #(-> % :interface :declarations) components))))
   :implemented-by (vec (sort (map :name components)))})

(defn interfaces [components]
  "Calculates all interfaces, which are all functions that are declared
   for each component interface."
  (let [grouped-components (group-by #(-> % :interface :name) components)
        single-components (filter #(= (-> % second count) 1) grouped-components)
        multi-components (filter #(> (-> % second count) 1) grouped-components)
        single-interfaces (mapv ->interface single-components)
        multi-interfaces (map ->multi-interface multi-components)]
    (vec (sort-by :name (concat single-interfaces multi-interfaces)))))
