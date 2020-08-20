(ns polylith.clj.core.user-input.core
  (:require [clojure.string :as str]
            [polylith.clj.core.user-input.params :as params]
            [polylith.clj.core.util.interfc :as util]))

(defn profile? [arg]
  (str/starts-with? arg "+"))

(defn active-dev-profiles [unnamed-args]
  (let [profiles (filter profile? unnamed-args)]
    (if (empty? profiles)
      #{"default"}
      (set (map #(subs % 1) profiles)))))

(defn selected-environments [env]
  (if (coll? env)
    env
    (if (nil? env)
      []
      [env])))

(defn extract-params [args]
  (let [{:keys [named-args unnamed-args]} (params/extract args)
        {:keys [name
                top-ns
                env
                flag
                brick
                interface
                loc
                all
                all-bricks
                test-env]} named-args
        unnamed (rest unnamed-args)]
    (util/ordered-map :cmd (first args)
                      :arg1 (second args)
                      :name name
                      :top-ns top-ns
                      :env env
                      :flag flag
                      :brick brick
                      :interface interface
                      :loc loc
                      :run-all? (or (= "true" all)
                                    (= "true" all-bricks))
                      :run-env-tests? (or (= "true" all)
                                          (= "true" test-env))
                      :active-dev-profiles (active-dev-profiles unnamed)
                      :selected-environments (-> env selected-environments set)
                      :unnamed-args unnamed)))
