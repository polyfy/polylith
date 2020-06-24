(ns polylith.validate.m106-multiple-interface-occurrences
  (:require [clojure.string :as str]
            [polylith.util.interface :as util]
            [polylith.common.interface.color :as color]))

(defn env-error [[interface interface-components] env-name test?]
  (when (and (not test?)
             (> (count interface-components) 1))
    (let [component-names (mapv second interface-components)
          message (str "More than one component that implements the " interface
                       " interface was found in the " env-name " environment: "
                       (str/join ", " component-names))
          colorized-msg (str "More than one component that implements the " (color/interface interface)
                             " interface was found in the " (color/environment env-name) " environment: "
                              (color/component (str/join ", " component-names)))]
      [(util/ordered-map :type "error"
                         :code 106
                         :message message
                         :colorized-message colorized-msg
                         :interface interface
                         :components component-names
                         :environment env-name)])))

(defn env-errors [{:keys [name test? component-names]} components]
  (let [env-components (filter #(contains? (set component-names)
                                           (:name %)) components)]
    (mapcat #(env-error % name test?)
            (group-by first (map (juxt #(-> % :interface :name) :name)
                                 env-components)))))

(defn errors [components environments]
  "Checks if more than one component implements the same interface in an environment."
  (mapcat #(env-errors % components) environments))
