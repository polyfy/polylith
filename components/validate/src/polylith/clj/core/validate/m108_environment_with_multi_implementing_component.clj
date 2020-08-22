(ns polylith.clj.core.validate.m108-environment-with-multi-implementing-component
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.path-finder.interfc.extract :as extract]
            [polylith.clj.core.path-finder.interfc.select :as select]
            [polylith.clj.core.path-finder.interfc.criterias :as c]))

(defn multi-impl-components [{:keys [implementing-components]}]
  (when (> (count implementing-components) 1) implementing-components))

(defn errors [ws-dir interfaces environments color-mode]
  (let [{:keys [src-paths test-paths]} (common/find-environment "dev" environments)
        path-entries (extract/path-entries ws-dir [src-paths test-paths])
        ; We can't use src-paths and test-paths from the dev environment
        ; because it has the paths from the active profile baked in.
        component-names (set (select/names path-entries c/component?))
        multi-components (set (mapcat multi-impl-components interfaces))
        illegal-components (sort (set/intersection component-names multi-components))
        components-msg (str/join ", " illegal-components)
        colored-comp_msg (str/join ", " (map #(color/component % color-mode) illegal-components))
        message (str "Components with an interface that are implemented by more than one component "
                     "is not allowed for the development environment. "
                     "They should be added to development profiles instead: ")]
    (when (-> illegal-components empty? not)
      [(util/ordered-map :type "error"
                         :code 108
                         :message (str message components-msg)
                         :colorized-message (str message colored-comp_msg))])))
