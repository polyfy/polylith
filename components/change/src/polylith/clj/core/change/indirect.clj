(ns polylith.clj.core.change.indirect
  (:require [clojure.set :as set]))

(defn brick-indirect-change [[brick {:keys [direct indirect]}] changed-bricks]
  (let [brick-changes (set (concat direct indirect))
        intersection (set/intersection brick-changes changed-bricks)]
    (when (and (-> intersection empty? not)
               (not (contains? changed-bricks brick)))
      brick)))

(defn project-indirect-changes [[project-name brick-changes] changed-bricks]
  [project-name (vec (sort (filter identity (map #(brick-indirect-change % changed-bricks) brick-changes))))])

(defn project-to-indirect-changes [deps changed-bricks]
  (into {} (map #(project-indirect-changes % changed-bricks) deps)))
