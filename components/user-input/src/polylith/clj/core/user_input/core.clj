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

(defn extract-params [args single-arg-commands]
  (let [{:keys [named-args unnamed-args]} (params/extract (rest args) single-arg-commands)
        {:keys [brick
                color-mode
                env
                get
                interface
                name
                since
                top-ns
                ws-dir
                all!
                all-bricks!
                brick!
                bricks!
                dev!
                env!
                loc!
                r!
                resources!]} named-args]
    (util/ordered-map :args (vec args)
                      :cmd (first args)
                      :get get
                      :brick brick
                      :color-mode color-mode
                      :interface interface
                      :name name
                      :since since
                      :top-ns top-ns
                      :ws-dir ws-dir
                      :search-for-ws-dir? (contains? (set args) "::")
                      :is-dev (= "true" dev!)
                      :show-brick? brick!
                      :show-bricks? bricks!
                      :show-env? (= "true" env!)
                      :show-loc? (= "true" loc!)
                      :run-all-tests? (= "true" all!)
                      :run-all-brick-tests? (or (= "true" all!)
                                                (= "true" all-bricks!))
                      :run-env-tests? (or (= "true" all!)
                                          (= "true" env!))
                      :show-resources? (or (= "true" r!)
                                           (= "true" resources!))
                      :active-dev-profiles (active-dev-profiles unnamed-args)
                      :selected-environments (selected-environments env dev!)
                      :unnamed-args (vec unnamed-args))))
