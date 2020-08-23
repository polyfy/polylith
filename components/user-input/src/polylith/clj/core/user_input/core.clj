(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.user-input.params :as params]
            [polylith.clj.core.util.interfc :as util]))

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
  (let [{:keys [named-args unnamed-args]} (params/extract args)
        {:keys [name
                top-ns
                env
                flag
                brick
                interface
                loc!
                all!
                all-bricks!
                dev!
                env!]} named-args
        unnamed (rest unnamed-args)]
    (util/ordered-map :args args
                      :cmd (first args)
                      :arg1 (second args)
                      :name name
                      :top-ns top-ns
                      :brick brick
                      :interface interface
                      :show-loc? (= "true" loc!)
                      :run-all? (or (= "true" all!)
                                    (= "true" all-bricks!))
                      :run-env-tests? (or (= "true" all!)
                                          (= "true" env!))
                      :show-resources-flag? (contains? #{"r" "resources"} flag)
                      :active-dev-profiles (active-dev-profiles unnamed)
                      :selected-environments (selected-environments env dev!)
                      :unnamed-args unnamed)))
