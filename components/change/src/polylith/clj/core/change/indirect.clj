(ns polylith.clj.core.change.indirect
  (:require [clojure.set :as set]))

(defn brick-indirect-change [[brick {:keys [direct indirect]}] changed-components]
  (let [brick-changes (set (concat direct indirect))
        intersection (set/intersection brick-changes changed-components)]
    (when (-> intersection empty? not)
      brick)))

(defn env-indirect-changes [[env brick-changes] changed-components]
  [env (vec (sort (filter identity (map #(brick-indirect-change % changed-components) brick-changes))))])

(defn indirect-changes [deps changed-components]
  (into {} (map #(env-indirect-changes % (set changed-components)) deps)))
