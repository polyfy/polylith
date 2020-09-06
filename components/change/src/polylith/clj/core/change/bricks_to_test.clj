(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn bricks-to-test-for-env [{:keys [name run-tests? test-base-names test-component-names]}
                              changed-components
                              changed-bases
                              env->indirect-changes
                              run-all-brick-tests?]
  (let [brick-names (set (concat test-base-names test-component-names))
        changed-bricks (if run-tests?
                         (if run-all-brick-tests?
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components
                                                          changed-bases
                                                          (env->indirect-changes name)))))
                         #{})]
    [name (vec (sort changed-bricks))]))

(defn env->bricks-to-test [environments changed-components changed-bases env->indirect-changes run-all-brick-tests?]
  (into {} (map #(bricks-to-test-for-env % changed-components changed-bases env->indirect-changes run-all-brick-tests?)
                environments)))
