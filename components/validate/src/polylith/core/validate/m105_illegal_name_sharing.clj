(ns polylith.core.validate.m105-illegal-name-sharing
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.core.util.interfc :as util]
            [polylith.core.util.interfc.color :as color]))

(defn errors [interface-names components bases color-mode]
  "A base are not allowed to share the name of an interface or component."
  (let [component-names (set (map :name components))
        base-names (set (map :name bases))
        interface-names-set (set/intersection (set interface-names) base-names)
        component-names-set (set/intersection component-names base-names)
        base-names-set (vec (set (concat interface-names-set component-names-set)))]
    (if (empty? base-names-set)
      []
      (let [message (str "A base can't have the same name as an interface or component: "
                         (str/join ", " (sort base-names-set)))
            colorized-msg (str "A base can't have the same name as an interface or component: "
                               (color/base (str/join ", " (sort base-names-set)) color-mode))]
        [(util/ordered-map :type "error"
                           :code 105
                           :message message
                           :colorized-message colorized-msg
                           :interfaces (vec interface-names-set)
                           :components (vec component-names-set)
                           :bases (vec base-names-set))]))))
