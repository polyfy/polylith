(ns polylith.clj.core.validator.m108-project-with-multi-implementing-component
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.path-finder.interface.criterias :as c]))

(defn multi-impl-components [{:keys [implementing-components]}]
  (when (> (count implementing-components) 1) implementing-components))

(defn errors [interfaces projects disk-paths color-mode]
  (let [{:keys [unmerged]} (common/find-project "dev" projects)
        {:keys [src-paths test-paths]} unmerged
        path-entries (extract/path-entries [src-paths test-paths] disk-paths)
        ; We can't use src-paths and test-paths from the dev project
        ; because it has the paths from the active profile baked in.
        component-names (set (select/names path-entries c/component?))
        multi-components (set (mapcat multi-impl-components interfaces))
        illegal-components (sort (set/intersection component-names multi-components))
        components-msg (str/join ", " (map #(color/component % color-mode) illegal-components))
        message (str "Components with an interface that is implemented by more than one component "
                     "is not allowed for the " (color/project "development" color-mode) " project. "
                     "They should be added to development profiles instead: " components-msg)]
    (when (-> illegal-components empty? not)
      [(util/ordered-map :type "error"
                         :code 108
                         :message (color/clean-colors message)
                         :colorized-message message)])))
