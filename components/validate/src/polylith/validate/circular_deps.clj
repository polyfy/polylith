(ns polylith.validate.circular-deps
  (:require [clojure.string :as str]
            [polylith.util.interface :as util]))

(defn interface-circular-deps [interface-name completed-deps interface->deps path]
  (if (contains? completed-deps interface-name)
    {:error (conj path interface-name)}
    (mapcat #(interface-circular-deps % (conj completed-deps interface-name) interface->deps (conj path interface-name))
            (interface->deps interface-name))))

(defn component-circular-deps [{:keys [name]} env-name interface->deps iname->component-name]
  (let [deps-chain (map iname->component-name
                        (-> (interface-circular-deps name #{} interface->deps [])
                            first second))
        message (str "Circular dependencies was found in the " env-name " environment: " (str/join " > " deps-chain))]
    (when (-> deps-chain empty? not)
      [(util/ordered-map :type "error"
                         :code 104
                         :message message
                         :components (vec (sort (set deps-chain)))
                         :environment env-name)])))

(defn environment-circular-deps [{:keys [name component-names]} interfaces components]
  "Calculates circular dependencies for an environment."
  (let [cname->interface-deps (into {} (map (juxt :name :interface-deps) components))
        cname->interface-name (into {} (map (juxt :name #(-> % :interface :name)) components))
        iname->component-name (into {} (map (juxt #(-> % :interface :name) :name)
                                            (filter #(contains? (set component-names) (:name %))
                                                    components)))
        interface->deps (into {} (map (juxt cname->interface-name
                                            cname->interface-deps)
                                      component-names))
        circular-deps (mapcat #(component-circular-deps % name interface->deps iname->component-name)
                              interfaces)]
    (when circular-deps
      circular-deps)))

(defn errors [interfaces components environments]
  (when-let [errors (first
                      (sort-by #(-> % :bricks count)
                        (mapcat #(environment-circular-deps % interfaces components)
                                (filter #(-> % :test? not)
                                        environments))))]
    [errors]))
