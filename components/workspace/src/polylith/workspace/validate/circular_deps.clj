(ns polylith.workspace.validate.circular-deps
  (:require [clojure.string :as str]))

(defn interface-deps [{:keys [name dependencies]}]
  [name (set dependencies)])

(defn interface-circular-deps [interface-name completed-deps interface->deps path]
  (if (contains? completed-deps interface-name)
    {:error (conj path interface-name)}
    (mapcat #(interface-circular-deps % (conj completed-deps interface-name) interface->deps (conj path interface-name))
            (interface->deps interface-name))))

(defn component-circular-deps [interface-name components]
  (let [interface->deps (into {} (map interface-deps components))]
    (-> (interface-circular-deps interface-name #{} interface->deps [])
        first second)))

(defn errors [interface-names components]
  "Makes sure there are no cirkular dependencies between components."
  (let [deps-chain (first (sort-by count
                                   (filter identity
                                           (map #(component-circular-deps % components)
                                                interface-names))))]
    (if (nil? deps-chain)
      []
      [(str "Circular dependencies was found: " (str/join " > " deps-chain))])))
