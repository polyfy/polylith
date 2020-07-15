(ns polylith.clj.core.change.environment
  (:require [clojure.set :as set]))

(defn changed-env [{:keys [name component-names base-names]} changed-bricks]
  (let [env-bricks (set (concat component-names base-names))
        changed-bricks-in-env (set/intersection changed-bricks env-bricks)]
    (when (-> changed-bricks-in-env empty? not)
      name)))

(defn indirectly-changed-environments [environments changed-bricks]
  (set (filter identity (map #(changed-env % changed-bricks) environments))))
