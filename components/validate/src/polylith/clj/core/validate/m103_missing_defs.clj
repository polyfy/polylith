(ns polylith.clj.core.validate.m103-missing-defs
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [polylith.clj.core.validate.shared :as shared]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.util.interfc :as util]))

(defn ->data-ifc [{:keys [definitions]}]
  (set (filter #(= "data" (:type %)) definitions)))

(defn component-data-defs [interface component]
  (let [data-defs (->data-ifc interface)
        comp-defs (->data-ifc (:interface component))
        missing-defs (set/difference data-defs comp-defs)]
    (when (-> missing-defs empty? not)
      [(str/join ", " (map shared/full-name missing-defs))])))

(defn function->id [{:keys [name parameters]}]
  [name (count parameters)])

(defn ->function [{:keys [sub-ns name parameters]}]
  (str (shared/full-name sub-ns name) "[" (str/join " " parameters) "]"))

(defn function-or-macro? [{:keys [type]}]
  (not= "data" type))

(defn functions-and-macros [{:keys [definitions]}]
  (set (filter function-or-macro? definitions)))

(defn component-fn-defs [component interface-functions]
  (let [component-functions-and-macros (-> component :interface functions-and-macros)
        missing-functions-and-macros (set/difference interface-functions component-functions-and-macros)]
    (when (-> missing-functions-and-macros empty? not)
      (vec (sort (map ->function missing-functions-and-macros))))))

(defn component-error [interface {:keys [name] :as component} interface-functions color-mode]
  (let [component-defs (concat (component-data-defs interface component)
                               (component-fn-defs component interface-functions))]
    (when (-> component-defs empty? not)
      (let [message (str "Missing definitions in " name "'s interface: "
                         (str/join ", " component-defs))
            colorized-msg (str "Missing definitions in "  (color/component name color-mode) "'s interface: "
                               (str/join ", " component-defs))]
        [(util/ordered-map :type "error"
                           :code 103
                           :message message
                           :colorized-message colorized-msg
                           :components [name])]))))

(defn interface-errors [{:keys [implementing-components] :as interface}
                        name->component color-mode]
  (let [interface-functions (set (mapcat second
                                         (filter #(= 1 (-> % second count) 1)
                                                 (group-by function->id
                                                           (functions-and-macros interface)))))
        ifc-components (map name->component implementing-components)]
    (mapcat #(component-error interface % interface-functions color-mode)
            ifc-components)))

(defn errors [interfaces components color-mode]
  (let [name->component (into {} (map (juxt :name identity) components))]
    (vec (mapcat #(interface-errors % name->component color-mode)
                 interfaces))))
