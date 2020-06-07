(ns polylith.workspace.validate.missing-functions-and-macros
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn function->id [{:keys [name signature]}]
  [name (count signature)])

(defn ->function [{:keys [name signature]}]
  (str name "[" (str/join " " signature) "]"))

(defn function-or-macro? [{:keys [type]}]
  (not= 'data type))

(defn functions-and-macros [{:keys [declarations]}]
  (set (filter function-or-macro? declarations)))

(defn component-errors [component interface-functions]
  (let [component-functions-and-macros (-> component :interface functions-and-macros)
        missing-functions-and-macros (set/difference interface-functions component-functions-and-macros)
        funcs-and-macros (str/join ", " (sort (map ->function missing-functions-and-macros)))
        missing-types (set (map :type missing-functions-and-macros))
        types (str/join " and " (sort (mapv #(str (name %) "s") missing-types)))]
    (when (-> missing-functions-and-macros empty? not)
      [(str "Missing " types " in component " (:name component) ": " funcs-and-macros)])))

(defn interface-errors [interface name->component]
  (let [interface-functions (set (mapcat second
                                         (filter #(= 1 (-> % second count) 1)
                                                 (group-by function->id
                                                           (functions-and-macros interface)))))]
    (mapcat #(component-errors % interface-functions)
            (map name->component (:implemented-by interface)))))

(defn errors [interfaces components]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (vec (mapcat #(interface-errors % name->component) interfaces))))
