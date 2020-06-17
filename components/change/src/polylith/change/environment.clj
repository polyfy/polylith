(ns polylith.change.environment
  (:require [clojure.set :as set]))

(defn changed? [{:keys [components bases]} changed-components changed-bases]
  (let [changed-bricks (concat
                         (set/intersection (set components) (set changed-components))
                         (set/intersection (set bases) (set changed-bases)))]
    (-> changed-bricks empty? not)))

(defn changes [environments changed-components changed-bases]
  (mapv :name
    (filter #(changed? % changed-components changed-bases)
            environments)))
