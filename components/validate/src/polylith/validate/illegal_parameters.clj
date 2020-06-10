(ns polylith.validate.illegal-parameters
  (:require [clojure.string :as str]))

(defn function->id [{:keys [name parameters]}]
  [name (count parameters)])

(defn id->functions-or-macro [{:keys [definitions]}]
  (group-by function->id
            (filter #(not= "data" (:type %)) definitions)))

(defn ->function-or-macro [{:keys [name parameters]}]
  (str name "[" (str/join " " parameters) "]"))

(def types->message {#{"function"} "Function"
                     #{"macro"} "Macro"
                     #("function" "macro") "Function and macro"})

(defn function-warnings [[id [{:keys [name type parameters]}]] interface component-name name->component]
  (let [other-component-names (filterv #(not= % component-name)
                                       (:implementing-components interface))
        other-component (-> other-component-names first name->component)
        other-function (first ((-> other-component :interface id->functions-or-macro) id))]

    (when (and (-> other-function nil? not)
               (not= parameters (:parameters other-function)))
      (let [[comp1 comp2] (sort [component-name (:name other-component)])
            function-or-macro1 (str name "[" (str/join " " parameters) "]")
            function-or-macro2 (->function-or-macro other-function)
            functions-and-macros (sort [function-or-macro1 function-or-macro2])
            types (types->message (set [type (:type other-function)]))]
        [(str types " in the " comp1 " component "
              "is also defined in " comp2
              " but with a different parameter list: "
              (str/join ", " functions-and-macros))]))))

(defn duplicated-parameter-lists-error [component-name component-duplication]
  (str "Duplicated parameter lists found in the " component-name " component: "
       (str/join ", " (map ->function-or-macro component-duplication))))

(defn component-errors [component]
  (let [component-name (:name component)
        component-id->function (-> component :interface id->functions-or-macro)
        multi-id-functions (mapv second (filter #(> (-> % second count) 1) component-id->function))]
    (mapv #(duplicated-parameter-lists-error component-name %) multi-id-functions)))

(defn component-warnings [component interfaces name->component]
  (let [interface-name (-> component :interface :name)
        interface (first (filter #(= interface-name (:name %)) interfaces))
        component-name (:name component)
        component-id->function (-> component :interface id->functions-or-macro)
        single-id->functions (filter #(= (-> % second count) 1) component-id->function)]
    (mapcat #(function-warnings % interface component-name name->component) single-id->functions)))

(defn warnings [interfaces components]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (vec (sort (set (mapcat #(component-warnings % interfaces name->component) components))))))

(defn errors [components]
  (vec (mapcat component-errors components)))
