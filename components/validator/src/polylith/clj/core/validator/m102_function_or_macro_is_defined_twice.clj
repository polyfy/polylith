(ns polylith.clj.core.validator.m102-function-or-macro-is-defined-twice
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.shared :as shared]))

(defn duplicated-parameter-lists-error [component-name component-duplication color-mode]
  (let [message (str "Function or macro is defined twice in " (color/component component-name color-mode) ": "
                     (str/join ", " (map shared/->function-or-macro component-duplication)))]
    (util/ordered-map :type "error"
                      :code 102
                      :message (color/clean-colors message)
                      :colorized-message message
                      :components [component-name])))

(defn component-errors [component color-mode]
  (let [component-name (:name component)
        component-id->function (-> component :interface shared/id->functions-or-macro)
        multi-id-functions (mapv second (filter #(> (-> % second count) 1) component-id->function))]
    (mapv #(duplicated-parameter-lists-error component-name % color-mode) multi-id-functions)))

(defn errors [components color-mode]
  (vec (mapcat #(component-errors % color-mode) components)))
