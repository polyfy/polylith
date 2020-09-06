(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.user-input.params :as params]))

(defn profile? [arg]
  (str/starts-with? arg "+"))

(defn active-dev-profiles [unnamed-args]
  (set (map #(subs % 1)
            (filter profile? unnamed-args))))

(defn selected-environments [env dev!]
  (let [envs (if (coll? env)
               env
               (if (nil? env)
                 []
                 [env]))]
    (set (if dev!
           (conj envs "dev")
           envs))))

(defn extract-params [args]
  (let [{:keys [named-args unnamed-args]} (params/extract (rest args))
        {:keys [brick
                color-mode
                env
                get
                interface
                name
                src
                top-ns
                ws-dir
                all!
                all-bricks!
                dev!
                env!
                lib!
                loc!]} named-args]
    (util/ordered-map :args args
                      :cmd (first args)
                      :arg1 (second args)
                      :get get
                      :brick brick
                      :color-mode color-mode
                      :interface interface
                      :name name
                      :top-ns top-ns
                      :ws-dir ws-dir
                      :show-loc? (= "true" loc!)
                      :show-lib? (= "true" lib!)
                      :run-all-brick-tests? (or (= "true" all!)
                                                (= "true" all-bricks!))
                      :run-env-tests? (or (= "true" all!)
                                          (= "true" env!))
                      :show-resources? (contains? #{"r" "resources"} src)
                      :active-dev-profiles (active-dev-profiles unnamed-args)
                      :selected-environments (selected-environments env dev!)
                      :unnamed-args (vec unnamed-args))))
