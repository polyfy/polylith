(ns polylith.validate.circular-deps
  (:require [clojure.string :as str]))

(defn interface-circular-deps [interface-name completed-deps interface->deps path]
  (if (contains? completed-deps interface-name)
    {:error (conj path interface-name)}
    (mapcat #(interface-circular-deps % (conj completed-deps interface-name) interface->deps (conj path interface-name))
            (interface->deps interface-name))))

(defn component-circular-deps [{:keys [name]} interface->deps]
  (-> (interface-circular-deps name #{} interface->deps [])
      first second))

(defn errors [interfaces]
  "Makes sure there are no circular dependencies between components (via their interfaces).
   The calculations are based on the interface dependencies which are the set of
   all dependencies taken from the components that implement them."
  (let [interface->deps (into {} (map (juxt :name :implementing-deps) interfaces))
        deps-chain (first (sort-by count
                                   (filter identity
                                           (map #(component-circular-deps % interface->deps)
                                                interfaces))))]
    (if (nil? deps-chain)
      []
      [(str "Circular dependencies was found: " (str/join " > " deps-chain))])))
