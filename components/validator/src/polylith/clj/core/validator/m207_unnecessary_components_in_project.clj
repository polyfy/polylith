(ns polylith.clj.core.validator.m207-unnecessary-components-in-project
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn component-deps [[ _ {:keys [src test]}]]
  (concat (:direct src)
          (:indirect src)
          (:direct test)
          (:indirect test)))

(defn warning [{:keys [is-dev name component-names deps]} project->settings check-dev color-mode]
  "Warns if a component is not used by any brick in the project for all
   project except development (which can be included by passing in :dev).
   Components can be excluded from the check by putting it in the :necessary
   vector for a project in :projects in workspace.edn."
  (let [{:keys [src test]} component-names
        necessary (-> (project->settings name) :necessary set)
        defined-components (set/difference (set (concat src test)) necessary)
        used-components (set (mapcat component-deps deps))
        unused-components (str/join ", " (map #(color/component % color-mode)
                                              (sort (set/difference defined-components used-components))))]
    (when (and (or check-dev (not is-dev))
               (seq unused-components))
      (let [message (str "Unnecessary components were found in the " (color/project name color-mode)
                         " project and may be removed: " unused-components)]
        [(util/ordered-map :type "warning"
                           :code 207
                           :message (color/clean-colors message)
                           :colorized-message message)]))))

(defn warnings [cmd settings projects dev? color-mode]
  (let [project->settings (:projects settings)
        check-dev (and dev? (= "check" cmd))]
    (mapcat #(warning % project->settings check-dev color-mode) projects)))
