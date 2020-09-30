(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn bricks-to-test-for-env [{:keys [name is-run-tests test-base-names test-component-names]}
                              changed-environments
                              changed-components
                              changed-bases
                              env->indirect-changes
                              is-run-all-brick-tests]
  (let [test-environment? (contains? (set changed-environments) name)
        brick-names (set (concat test-base-names test-component-names))
        changed-bricks (if is-run-tests
                         (if (or is-run-all-brick-tests test-environment?)
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components
                                                          changed-bases
                                                          (env->indirect-changes name)))))
                         #{})]
    [name (vec (sort changed-bricks))]))

(defn env->bricks-to-test [changed-environments environments changed-components changed-bases env->indirect-changes is-run-all-brick-tests]
  (into {} (map #(bricks-to-test-for-env % changed-environments changed-components changed-bases env->indirect-changes is-run-all-brick-tests)
                environments)))
