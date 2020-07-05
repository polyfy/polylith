(ns polylith.clj.core.change.environment
  (:require [clojure.set :as set]))

(defn changed? [{:keys [component-names base-names]} changed-components changed-bases]
  (let [changed-bricks (concat
                         (set/intersection (set component-names) (set changed-components))
                         (set/intersection (set base-names) (set changed-bases)))]
    (-> changed-bricks empty? not)))

(defn changes [environments changed-components changed-bases]
  (mapv :name
    (filter #(changed? % changed-components changed-bases)
            environments)))
