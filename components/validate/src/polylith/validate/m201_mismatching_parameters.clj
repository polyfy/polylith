(ns polylith.validate.m201-mismatching-parameters
  (:require [clojure.string :as str]
            [polylith.util.interface :as util]
            [polylith.validate.shared :as shared]))

(def types->message {#{"function"} "Function"
                     #{"macro"} "Macro"
                     #("function" "macro") "Function and macro"})

(defn function-warnings [[id [{:keys [sub-ns name type parameters]}]] interface component-name name->component]
  (let [other-component-names (filterv #(not= % component-name)
                                       (:implementing-components interface))
        other-component (-> other-component-names first name->component)
        other-function (first ((-> other-component :interface shared/id->functions-or-macro) id))]

    (when (and (-> other-function nil? not)
               (not= parameters (:parameters other-function)))
      (let [[comp1 comp2] (sort [component-name (:name other-component)])
            function-or-macro1 (shared/->function-or-macro sub-ns name parameters)
            function-or-macro2 (shared/->function-or-macro other-function)
            functions-and-macros (sort [function-or-macro1 function-or-macro2])
            types (types->message (set [type (:type other-function)]))
            message (str types " in the " comp1 " component "
                         "is also defined in " comp2
                         " but with a different parameter list: "
                         (str/join ", " functions-and-macros))]
        [(util/ordered-map :type "warning"
                           :code 201
                           :message message
                           :components [comp1 comp2])]))))

(defn component-warnings [component interfaces name->component]
  (let [interface-name (-> component :interface :name)
        interface (first (filter #(= interface-name (:name %)) interfaces))
        component-name (:name component)
        component-id->function (-> component :interface shared/id->functions-or-macro)
        single-id->functions (filter #(= (-> % second count) 1) component-id->function)]
    (mapcat #(function-warnings % interface component-name name->component) single-id->functions)))

(defn warnings [interfaces components]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (set (mapcat #(component-warnings % interfaces name->component) components))))
