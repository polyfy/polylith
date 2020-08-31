(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interfc :as util]
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
  (let [{:keys [named-args unnamed-args]} (params/extract args)
        {:keys [name
                ws-dir
                top-ns
                env
                src
                brick
                interface
                lib!
                loc!
                all!
                all-bricks!
                dev!
                env!
                color-mode]} named-args
        unnamed (vec (rest unnamed-args))]
    (util/ordered-map :args args
                      :cmd (first args)
                      :arg1 (second args)
                      :name name
                      :ws-dir ws-dir
                      :top-ns top-ns
                      :brick brick
                      :interface interface
                      :show-loc? (= "true" loc!)
                      :show-lib? (= "true" lib!)
                      :run-all? (or (= "true" all!)
                                    (= "true" all-bricks!))
                      :run-env-tests? (or (= "true" all!)
                                          (= "true" env!))
                      :color-mode color-mode
                      :show-resources? (contains? #{"r" "resources"} src)
                      :active-dev-profiles (active-dev-profiles unnamed)
                      :selected-environments (selected-environments env dev!)
                      :unnamed-args unnamed)))
