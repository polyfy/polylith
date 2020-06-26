(ns polylith.validate.m107-missing-componens-in-environment
  (:require [clojure.set :as set]
            [polylith.util.interface :as util]
            [polylith.common.interface.color :as color]
            [clojure.string :as str])
  (:refer-clojure :exclude [bases]))

(defn interface-deps [interface processed interface->deps]
  (when (not (contains? @processed interface))
    (swap! processed conj interface)
    (doseq [ifc (interface->deps interface)]
      (interface-deps ifc processed interface->deps))))

(defn dependencies [interface interface->deps]
  (let [processed (atom #{})]
    (interface-deps interface processed interface->deps)
    (conj @processed interface)))

(defn env-status [{:keys [name component-names base-names]} components bases]
  (let [base-name->interface-deps (into {} (map (juxt :name :interface-deps) bases))
        component-name->interface-name (into {} (map (juxt :name #(-> % :interface :name)) components))
        component-name->interface-deps (into {} (map (juxt :name :interface-deps) components))
        interface->deps (into {} (map (juxt #(-> % :interface :name) :interface-deps) components))
        env-interfaces (set (map component-name->interface-name component-names))
        env-interface-deps (set (concat (mapcat base-name->interface-deps base-names)
                                        (mapcat component-name->interface-deps component-names)))
        all-env-interface-deps (set (concat env-interfaces
                                            (mapcat #(dependencies % interface->deps) env-interface-deps)))
        missing-interface-names (set/difference all-env-interface-deps env-interfaces)]
    {:env name
     :missing-interface-names missing-interface-names}))

(defn missing-components-error [env interface-names]
  (let [interfaces (str/join ", " interface-names)
        message (str "Missing components in the " env " environment for these interfaces: " interfaces)
        colorized-msg (str "Missing components in the " (color/environment env) " environment "
                            "for these interfaces: " (color/interface interfaces))]
    [(util/ordered-map :type "error"
                       :code 107
                       :message message
                       :colorized-message colorized-msg
                       :interfaces interface-names
                       :environment env)]))

(defn env-error [{:keys [env missing-interface-names]}]
  (when (-> missing-interface-names empty? not)
    (missing-components-error env missing-interface-names)))

(defn errors [components bases environments]
  (mapcat env-error
          (map #(env-status % components bases)
               (filter #(-> % :test? not) environments))))
