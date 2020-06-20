(ns polylith.validate.multiple-interface-occurrences
  (:require [clojure.string :as str]))

(defn env-error [[interface interface-components] env-name]
  (when (> (count interface-components) 1)
    [(str "More than one component that implements the " interface
          " interface was found in the " env-name " environment: "
           (str/join ", " (map second interface-components)))]))

(defn env-errors [{:keys [name component-names]} components]
  (let [env-components (filter #(contains? (set component-names)
                                           (:name %)) components)]
    (mapcat #(env-error % name)
            (group-by first (map (juxt #(-> % :interface :name) :name)
                                 env-components)))))

(defn errors [components environments]
  "Checks if more than one component implements the same interface in an environment."
  (mapcat #(env-errors % components) environments))
