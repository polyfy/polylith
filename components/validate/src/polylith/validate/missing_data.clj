(ns polylith.validate.missing-data
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.validate.shared :as shared]))

(defn ->data-ifc [{:keys [definitions]}]
  (set (filter #(= "data" (:type %)) definitions)))

(defn component-errors [interface component]
  (let [data-defs (->data-ifc interface)
        comp-defs (->data-ifc (:interface component))
        missing-defs (set/difference data-defs comp-defs)
        data-defs (str/join ", " (map shared/full-name missing-defs))]
    (when (-> missing-defs empty? not)
      [(str "Missing definitions in the interface of " (:name component) ": " data-defs)])))

(defn interface-errors [{:keys [implementing-components] :as interface} name->component]
  (let [ifc-components (map name->component implementing-components)]
    (mapcat #(component-errors interface %) ifc-components)))

(defn errors [interfaces components]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (mapcat #(interface-errors % name->component) interfaces)))
