(ns ^:no-doc polylith.clj.core.validator.m207-unnecessary-components-in-project
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn component-deps [[ _ {:keys [src test]}]]
  (concat (:direct src)
          (:indirect src)
          (:direct test)
          (:indirect test)))

(defn warning [{:keys [is-dev name necessary component-names deps]} check-dev color-mode]
  "Warns if a component is not used by any brick in the project for all
   project except development (which can be included by passing in :dev).
   Components can be excluded from the check by putting it in the :necessary
   vector for a project in :projects in workspace.edn."
  (let [{:keys [src test]} component-names
        defined-components (set/difference (set (concat src test)) (set necessary))
        used-components (set (mapcat component-deps deps))
        unused-components (str/join ", " (map #(color/component % color-mode)
                                              (sort (set/difference defined-components used-components))))]
    (when (and (or check-dev (not is-dev))
               (seq unused-components))
      (let [message (str "Unnecessary components were found in the " (color/project name color-mode)
                         " project and may be removed: " unused-components ". To ignore this warning,"
                         " execute 'poly help check' and follow the instructions for warning 207.")]
        [(util/ordered-map :type "warning"
                           :code 207
                           :message (color/clean-colors message)
                           :colorized-message message)]))))

(defn warnings [cmd projects dev? color-mode]
  (let [check-dev (and dev? (= "check" cmd))]
    (mapcat #(warning % check-dev color-mode) projects)))
