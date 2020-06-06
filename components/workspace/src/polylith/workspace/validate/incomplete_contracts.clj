(ns polylith.workspace.validate.incomplete-contracts
  (:require [clojure.string :as str]))

(defn function->id [{:keys [name signature]}]
  [name (count signature)])

(defn id->functions [{:keys [declarations]}]
  (group-by function->id
            (filterv #(= 'function (:type %)) declarations)))

(defn ->function [{:keys [name signature]}]
  (str name "[" (str/join " " signature) "]"))

(defn function-warnings [[id [{:keys [name signature]}]] interface component-name name->component]
  (let [other-component-names (filterv #(not= % component-name)
                                       (:implemented-by interface))
        other-component (-> other-component-names first name->component)
        other-function (first ((id->functions (:interface other-component)) id))
        [comp1 comp2] (sort [component-name (:name other-component)])
        functions (sort [(->function other-function)
                         (str name "[" (str/join " " signature) "]")])]
    (when other-function
      [(str "Function in component " comp1 " "
            "is also defined in " comp2
            " but with a different parameter list: "
            (str/join ", " functions))])))

(defn duplicated-signature-error [component-name component-duplication]
  (str "Duplicated signatures found in the " component-name " component: "
       (str/join ", " (map ->function component-duplication))))

(defn component-errors [component]
  (let [component-name (:name component)
        component-id->function (-> component :interface id->functions)
        multi-id-functions (mapv second (filter #(> (-> % second count) 1) component-id->function))]
    (when (-> multi-id-functions empty? not)
      (mapv #(duplicated-signature-error component-name %) multi-id-functions))))

(defn component-warnings [component interfaces name->component]
  (let [interface-name (-> component :interface :name)
        interface (first (filter #(= interface-name (:name %)) interfaces))
        component-name (:name component)
        component-id->function (-> component :interface id->functions)
        single-id->functions (filter #(= (-> % second count) 1) component-id->function)]
    (mapcat #(function-warnings % interface component-name name->component) single-id->functions)))

(defn warnings [interfaces components]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (mapcat #(component-warnings % interfaces name->component) components)))

(defn errors [components]
  (mapcat component-errors components))
