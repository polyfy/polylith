(ns polylith.clj.core.change.indirect
  (:require [clojure.set :as set]))

(defn brick-source-indirect-change [brick {:keys [direct indirect]} changed-bricks]
  (let [brick-changes (set (concat direct indirect))
        intersection (set/intersection brick-changes changed-bricks)]
    (when (and (-> intersection empty? not)
               (not (contains? changed-bricks brick)))
      [brick])))

(defn brick-indirect-src-change [[brick {:keys [src]}] changed-bricks]
  (brick-source-indirect-change brick src changed-bricks))

(defn brick-indirect-test-change [[brick {:keys [test]}] changed-bricks]
  (brick-source-indirect-change brick test changed-bricks))

(defn project-indirect-changes [[project-name deps] changed-bricks]
  [project-name {:src (vec (sort (mapcat #(brick-indirect-src-change % changed-bricks) deps)))
                 :test (vec (sort (mapcat #(brick-indirect-test-change % changed-bricks) deps)))}])

(defn project-to-indirect-changes [projects-deps changed-bricks]
  "Calculates the bricks that are indirectly changed within the given project,
   directly changes excluded."
  (into {} (map #(project-indirect-changes % changed-bricks) projects-deps)))
