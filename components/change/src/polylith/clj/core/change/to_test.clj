(ns polylith.clj.core.change.to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.change.environment :as env]))

(defn bricks-to-test-for-env [{:keys [name dev? test-base-names test-component-names]}
                              changed-components
                              changed-bases
                              env->indirect-changes
                              enable-dev?]
  (let [changed-bricks (if (or (not dev?) enable-dev?)
                         (set (concat changed-components changed-bases (env->indirect-changes name)))
                         #{})
        brick-names (set (concat test-base-names test-component-names))]
    [name (vec (sort (set/intersection brick-names changed-bricks)))]))

(defn env->bricks-to-test [environments changed-components changed-bases env->indirect-changes enable-dev?]
  (into {} (map #(bricks-to-test-for-env % changed-components changed-bases env->indirect-changes enable-dev?)
                environments)))

(defn environments-to-test [environments changed-bricks changed-environments]
  (let [indirectly-changed (env/indirectly-changed-environment-names environments changed-bricks)
        changed-envs (set (concat changed-environments indirectly-changed))
        environments-with-test-dir (set (map :name (filter :has-test-dir? environments)))]
    (vec (sort (set/intersection environments-with-test-dir changed-envs)))))
