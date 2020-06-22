(ns polylith.validate.illegal-name-sharing
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.util.interface :as util]))

(defn errors [interface-names components bases]
  "A base are not allowed to share the name of an interface or component."
  (let [component-names (set (map :name components))
        base-names (set (map :name bases))
        interface-names-set (set/intersection (set interface-names) base-names)
        component-names-set (set/intersection component-names base-names)
        base-names-set (vec (set (concat interface-names-set component-names-set)))]
    (if (empty? base-names-set)
      []
      (let [message (str "A base can't have the same name as an interface or component: "
                         (str/join ", " (sort base-names-set)))]
        [(util/ordered-map :type "error"
                           :code 105
                           :message message
                           :interfaces (vec interface-names-set)
                           :components (vec component-names-set)
                           :bases (vec base-names-set))]))))
