(ns ^:no-doc polylith.clj.core.validator.m201-mismatching-argument-lists
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface.color :as color]))

(def types->message {#{"function"} "Function"
                     #{"macro"} "Macro"
                     #{"function" "macro"} "Function and macro"})

(defn function-warnings [[id [{:keys [sub-ns name type arglist]}]] interface component-name name->component color-mode]
  (let [other-component-names (filterv #(not= % component-name)
                                       (:implementing-components interface))
        other-component (-> other-component-names first name->component)
        other-function (first ((-> other-component :interface shared/id->functions-or-macro) id))]

    (when (and (-> other-function nil? not)
               (not= arglist (:arglist other-function)))
      (let [[comp1 comp2] (sort [component-name (:name other-component)])
            function-or-macro1 (shared/->function-or-macro sub-ns name arglist)
            function-or-macro2 (shared/->function-or-macro other-function)
            functions-and-macros (sort [function-or-macro1 function-or-macro2])
            types (types->message (set [type (:type other-function)]))
            message (str types " in the " (color/component comp1 color-mode) " component "
                         "is also defined in " (color/component comp2 color-mode)
                         " but with a different argument list: "
                         (str/join ", " functions-and-macros))]
        [(util/ordered-map :type "warning"
                           :code 201
                           :message (color/clean-colors message)
                           :colorized-message message
                           :components [comp1 comp2])]))))

(defn component-warnings [component interfaces name->component color-mode]
  (let [interface-name (-> component :interface :name)
        interface (first (filter #(= interface-name (:name %)) interfaces))
        component-name (:name component)
        component-id->function (-> component :interface shared/id->functions-or-macro)
        single-id->functions (filter #(= (-> % second count) 1) component-id->function)]
    (mapcat #(function-warnings % interface component-name name->component color-mode) single-id->functions)))

(defn warnings [interfaces components color-mode]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (set (mapcat #(component-warnings % interfaces name->component color-mode) components))))
