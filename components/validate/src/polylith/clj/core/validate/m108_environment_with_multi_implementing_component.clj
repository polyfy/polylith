(ns polylith.clj.core.validate.m108-environment-with-multi-implementing-component
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn multi-impl-components [{:keys [implementing-components]}]
  (when (> (count implementing-components) 1) implementing-components))

(defn errors [interfaces environments color-mode]
  (let [environment (common/find-environment "development" environments)
        multi-components (set (mapcat multi-impl-components interfaces))
        dev-envionments (-> environment :component-names set)
        illegal-components (sort (set/intersection dev-envionments multi-components))
        components-msg (str/join ", " illegal-components)
        colored-comp_msg (str/join ", " (map #(color/component % color-mode) illegal-components))
        message (str "Components with an interface that are implemented by more than one component "
                     "is not allowed for the development environment. "
                     "They should be added to development profiles instead: ")]
    [(util/ordered-map :type "error"
                       :code 108
                       :message (str message components-msg)
                       :colorized-message (str message colored-comp_msg))]))
