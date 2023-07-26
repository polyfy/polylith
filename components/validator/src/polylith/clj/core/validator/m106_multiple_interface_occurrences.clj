(ns ^:no-doc polylith.clj.core.validator.m106-multiple-interface-occurrences
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn project-error [[interface interface-components] project-name test? color-mode]
  (when (and (not test?)
             (> (count interface-components) 1))
    (let [component-names (mapv second interface-components)
          message (str "More than one component that implements the " (color/interface interface color-mode)
                       " interface was found in the " (color/project project-name color-mode) " project: "
                       (color/component (str/join ", " component-names) color-mode))]
      [(util/ordered-map :type "error"
                         :code 106
                         :message (color/clean-colors message)
                         :colorized-message message
                         :interface interface
                         :components component-names
                         :project project-name)])))

(defn project-errors [{:keys [name test? component-names]} components color-mode]
  (let [project-components (filter #(contains? (set (:src component-names))
                                               (:name %)) components)]
    (mapcat #(project-error % name test? color-mode)
            (filter first
              (group-by first (map (juxt #(-> % :interface :name) :name)
                                   project-components))))))

(defn errors [components projects color-mode]
  (mapcat #(project-errors % components color-mode)
          projects))
