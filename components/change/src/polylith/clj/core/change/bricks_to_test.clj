(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn bricks-to-test-for-project [{:keys [name is-run-tests base-names component-names]}
                                  settings
                                  changed-projects
                                  changed-components
                                  changed-bases
                                  project-to-indirect-changes
                                  is-run-all-brick-tests]
  (let [project-has-changed? (contains? (set changed-projects) name)
        selected-bricks-to-test (get-in settings [:projects name :test :include])
        all-brick-names (set (concat (:test base-names) (:test component-names)))
        ;; if the :test key is given for a project in workspace.edn, then only test the specified bricks,
        ;; otherwise, run tests for all bricks that has tests.
        brick-names (if selected-bricks-to-test
                      (set/intersection all-brick-names (set selected-bricks-to-test))
                      all-brick-names)
        changed-bricks (if is-run-tests
                         ;; if the project has changed (e.g. its configuration) - always run all brick tests
                         (if (or is-run-all-brick-tests project-has-changed?)
                           brick-names
                           (set/intersection brick-names
                                             (set (concat changed-components
                                                          changed-bases
                                                          (-> name project-to-indirect-changes :src)
                                                          (-> name project-to-indirect-changes :test)))))
                         #{})]
    [name (-> changed-bricks sort vec)]))

(defn project-to-bricks-to-test [changed-projects projects settings changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests]
  (into {} (map #(bricks-to-test-for-project % settings changed-projects changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests)
                projects)))

