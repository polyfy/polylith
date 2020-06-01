(ns polylith.workspace.validate.contracts-defined
  (:require [clojure.string :as str]))

(defn error-message [interface-name components]
  (let [components-with-ifc (filter #(= (-> % :interface :name) interface-name) components)
        contracts (filter #(= interface-name (:name %)) components-with-ifc)
        component-names (str/join ", " (map :name components-with-ifc))]
    (if (= 1 (count contracts))
      []
      [(str "No contract was found for the '" interface-name "' interface. "
            "Make sure you have a component named '" interface-name
            "' or specify a component as contract holder "
            "in the :polylith config and select one of: " component-names)])))

(defn errors [components]
  "One component needs to be the holder of the interface contract.
   Right now we assume it is the component (if any) that has the
   same name as the interface."
  (let [multiused-interfaces (set (mapv first (filter #(> (-> % second count) 1)
                                                      (group-by identity (map #(-> % :interface :name) components)))))]
    (vec (mapcat #(error-message % components) multiused-interfaces))))
