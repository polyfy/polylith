(ns ^:no-doc polylith.clj.core.change.indirect-changes
  (:require [clojure.set :as set]))

(defn brick-source-indirect-change [brick {:keys [direct indirect]} changed-bricks]
  (let [brick-deps (set (concat direct indirect))
        intersection (set/intersection brick-deps changed-bricks)]
    (when (and (-> intersection empty? not)
               (not (contains? changed-bricks brick)))
      [brick])))

(defn brick-indirect-src-change [[brick {:keys [src]}] changed-bricks]
  (brick-source-indirect-change brick src changed-bricks))

(defn brick-indirect-test-change [[brick {:keys [test]}] changed-bricks]
  (brick-source-indirect-change brick test changed-bricks))

(defn with-indirect-changes [{:keys [deps] :as project} changed-bricks]
  (assoc project :indirect-changes
                 {:src (vec (sort (mapcat #(brick-indirect-src-change % changed-bricks) deps)))
                  :test (vec (sort (mapcat #(brick-indirect-test-change % changed-bricks) deps)))}))
