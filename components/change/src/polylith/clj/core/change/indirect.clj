(ns polylith.clj.core.change.indirect
  (:require [clojure.set :as set]))

(defn brick-indirect-change [[brick {:keys [direct indirect]}] changed-bricks]
  (let [brick-changes (set (concat direct indirect))
        intersection (set/intersection brick-changes changed-bricks)]
    (when (and (-> intersection empty? not)
               (not (contains? changed-bricks brick)))
      brick)))

(defn env-indirect-changes [[env brick-changes] changed-bricks]
  [env (vec (sort (filter identity (map #(brick-indirect-change % changed-bricks) brick-changes))))])

(defn env-to-indirect-changes [deps changed-bricks]
  (into {} (map #(env-indirect-changes % changed-bricks) deps)))
