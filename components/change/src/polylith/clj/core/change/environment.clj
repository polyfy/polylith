(ns polylith.clj.core.change.environment
  (:require [clojure.set :as set]))

(defn changed-env [{:keys [name component-names base-names]} changed-bricks]
  (let [env-bricks (set (concat component-names base-names))
        intersection (set/intersection changed-bricks env-bricks)]
    (when (-> intersection empty? not)
      name)))

(defn changed-environments [environments changed-bricks]
  (set (filter identity (map #(changed-env % changed-bricks) environments))))
