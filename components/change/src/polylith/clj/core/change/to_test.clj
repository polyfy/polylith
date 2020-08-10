(ns polylith.clj.core.change.to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.change.environment :as env]))

(defn bricks-to-test-for-env [{:keys [name active? test-base-names test-component-names]}
                              changed-components
                              changed-bases
                              env->indirect-changes
                              run-all?]
  (let [brick-names (set (concat test-base-names test-component-names))
        changed-bricks (if active?
                         (if run-all?
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components changed-bases (env->indirect-changes name)))))
                         #{})]
    [name (vec (sort changed-bricks))]))

(defn env->bricks-to-test [environments changed-components changed-bases env->indirect-changes run-all?]
  (into {} (map #(bricks-to-test-for-env % changed-components changed-bases env->indirect-changes run-all?)
                environments)))

(defn environments-to-test [environments changed-bricks changed-environments run-env-tests?]
  (let [indirectly-changed (env/indirectly-changed-environment-names environments changed-bricks)
        changed-envs (if run-env-tests?
                       (set (concat changed-environments indirectly-changed))
                       #{})
        environments-with-test-dir (set (map :name (filter :has-test-dir? environments)))]
    (vec (sort (set/intersection environments-with-test-dir changed-envs)))))
