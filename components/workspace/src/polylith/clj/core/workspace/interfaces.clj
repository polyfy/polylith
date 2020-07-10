(ns polylith.clj.core.workspace.interfaces)

(defn ->interface [[_ [{:keys [name interface]}]]]
  {:name (:name interface)
   :type "interface"
   :definitions (:definitions interface)
   :implementing-components [name]})

(defn params [parameters]
  (mapv :name parameters))

(defn ->multi-interface [[interface-name components]]
  {:name interface-name
   :type "interface"
   :definitions (vec (sort-by (juxt :sub-ns :type :name params)
                              (set (mapcat #(-> % :interface :definitions) components))))
   :implementing-components (vec (sort (map :name components)))})

(defn calculate [components]
  "Calculates all interfaces, which are all definitions (data/function/macro)
   that are defined for all components that implements an interface."
  (let [grouped-components (group-by #(-> % :interface :name) components)
        single-components (filter #(= (-> % second count) 1) grouped-components)
        multi-components (filter #(> (-> % second count) 1) grouped-components)
        single-interfaces (mapv ->interface single-components)
        multi-interfaces (map ->multi-interface multi-components)]
    (vec (concat single-interfaces multi-interfaces))))
