(ns ^:no-doc polylith.clj.core.change.project
  (:require [clojure.set :as set]))

(defn changed-project [{:keys [name component-names base-names]} changed-bricks]
  (let [project-bricks (set (concat (:src component-names) (:src base-names)))
        changed-bricks-in-project (set/intersection changed-bricks project-bricks)]
    (when (-> changed-bricks-in-project empty? not)
      name)))

(defn indirectly-changed-project-names [projects changed-bricks]
  (set (filter identity (map #(changed-project % changed-bricks) projects))))
