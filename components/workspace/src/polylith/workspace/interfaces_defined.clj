(ns polylith.workspace.interfaces-defined
  (:require [clojure.set :as set]
            [clojure.string :as str]))

(defn errors [interface-names components]
  "One component needs to be the 'master' for the interface it has.
   Right now we assume it is the component (if any) that has the
   same as the interface, if more than one component implements
   that interface, or the component that implements the interface
   if only one component."
  (let [multiused-interfaces (set (mapv first (filter #(> (-> % second count) 1)
                                                      (group-by identity (map #(-> % :interface :name) components)))))
        defined-interfaces (set/intersection (set interface-names)
                                             multiused-interfaces)
        undefined-interfaces (str/join ", " (str/join (set/difference (set defined-interfaces))
                                                      multiused-interfaces))]
    (if (= multiused-interfaces defined-interfaces)
      []
      [(str "Undefined interfaces: " undefined-interfaces)])))
