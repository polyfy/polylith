(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn bricks-to-test-for-project [{:keys [name is-run-tests test-base-names test-component-names]}
                                  settings
                                  changed-projects
                                  changed-components
                                  changed-bases
                                  project-to-indirect-changes
                                  is-run-all-brick-tests]
  (let [test-project? (contains? (set changed-projects) name)
        only-test-bricks (get-in settings [:projects name :test])
        all-brick-names (set (concat test-base-names test-component-names))
        ;; if the :test key is given for a project in workspace.edn, then only test the specified bricks.
        brick-names (if only-test-bricks
                      (set/intersection all-brick-names (set only-test-bricks))
                      all-brick-names)
        changed-bricks (if is-run-tests
                         (if (or is-run-all-brick-tests test-project?)
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components
                                                          changed-bases
                                                          (project-to-indirect-changes name)))))
                         #{})]
    [name (-> changed-bricks sort vec)]))

(defn project-to-bricks-to-test [changed-projects projects settings changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests]
  (into {} (map #(bricks-to-test-for-project % settings changed-projects changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests)
                projects)))
