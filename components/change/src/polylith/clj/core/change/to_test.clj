(ns polylith.clj.core.change.to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.change.environment :as env]))

(defn bricks-to-test-for-env [{:keys [name test-base-names test-component-names]}
                              changed-components changed-bases env->indirect-changes]
  (let [changed-bricks (set (concat changed-components changed-bases (env->indirect-changes name)))
        brick-names (set (concat test-base-names test-component-names))]
    [name (vec (sort (set/intersection brick-names changed-bricks)))]))

(defn env->bricks-to-test [environments changed-components changed-bases env->indirect-changes]
  (into {} (map #(bricks-to-test-for-env % changed-components changed-bases env->indirect-changes)
                environments)))

(defn environments-to-test [environments changed-bricks changed-environments]
  (let [indirectly-changed (env/indirectly-changed-environment-names environments changed-bricks)
        changed-envs (set (concat changed-environments indirectly-changed))
        environments-with-test-dir (set (map :name (filter :has-test-dir? environments)))]
    (vec (sort (set/intersection environments-with-test-dir changed-envs)))))
