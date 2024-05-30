(ns ^:no-doc polylith.clj.core.change.indirect-changes
  (:require [clojure.set :as set]))

(defn indirect-change [brick-name {:keys [direct indirect]} changed-bricks]
  (let [brick-deps (set (concat direct indirect))
        changed-brick-deps (set/intersection brick-deps changed-bricks)]
    (when (and (seq changed-brick-deps)
               (not (contains? changed-bricks brick-name)))
      [brick-name])))

(defn indirect-src-change [[brick-name {:keys [src]}] changed-bricks]
  (indirect-change brick-name src changed-bricks))

(defn indirect-test-change [[brick-name {:keys [test]}] changed-bricks]
  (indirect-change brick-name test changed-bricks))

(defn with-indirect-changes [{:keys [deps] :as project} changed-bricks]
  (assoc project :indirect-changes
                 {:src (vec (sort (mapcat #(indirect-src-change % changed-bricks) deps)))
                  :test (vec (sort (mapcat #(indirect-test-change % changed-bricks) deps)))}))
