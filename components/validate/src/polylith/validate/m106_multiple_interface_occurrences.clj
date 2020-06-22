(ns polylith.validate.m106-multiple-interface-occurrences
  (:require [clojure.string :as str]
            [polylith.util.interface :as util]))

(defn env-error [[interface interface-components] env-name]
  (when (> (count interface-components) 1)
    (let [component-names (mapv second interface-components)
          message (str "More than one component that implements the " interface
                       " interface was found in the " env-name " environment: "
                        (str/join ", " component-names))]
      [(util/ordered-map :type "error"
                         :code 106
                         :message message
                         :interface interface
                         :components component-names
                         :environment env-name)])))

(defn env-errors [{:keys [name component-names]} components]
  (let [env-components (filter #(contains? (set component-names)
                                           (:name %)) components)]
    (mapcat #(env-error % name)
            (group-by first (map (juxt #(-> % :interface :name) :name)
                                 env-components)))))

(defn errors [components environments]
  "Checks if more than one component implements the same interface in an environment."
  (mapcat #(env-errors % components) environments))
