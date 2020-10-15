(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn bricks-to-test-for-project [{:keys [name is-run-tests test-base-names test-component-names]}
                                  changed-projects
                                  changed-components
                                  changed-bases
                                  project-to-indirect-changes
                                  is-run-all-brick-tests]
  (let [test-project? (contains? (set changed-projects) name)
        brick-names (set (concat test-base-names test-component-names))
        changed-bricks (if is-run-tests
                         (if (or is-run-all-brick-tests test-project?)
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components
                                                          changed-bases
                                                          (project-to-indirect-changes name)))))
                         #{})]
    [name (vec (sort changed-bricks))]))

(defn project-to-bricks-to-test [changed-projects projects changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests]
  (into {} (map #(bricks-to-test-for-project % changed-projects changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests)
                projects)))
