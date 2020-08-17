(ns polylith.clj.core.common.user-input
  (:require [polylith.clj.core.util.interfc.params :as params]
            [clojure.string :as str]))

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

(defn parameters [args]
  (let [{:keys [named-args unnamed-args]} (params/extract args)
        {:keys [name
                top-ns
                env
                brick
                interface
                loc
                all
                all-bricks
                test-env]} named-args]
    {:arg1 (first args)
     :name name
     :top-ns top-ns
     :env env
     :brick brick
     :interface interface
     :loc loc
     :run-all? (or (= "true" all)
                   (= "true" all-bricks))
     :run-env-tests? (or (= "true" all)
                         (= "true" test-env))
     :active-dev-profiles (active-dev-profiles unnamed-args)
     :selected-environments (-> env selected-environments set)
     :unnamed-args unnamed-args}))
