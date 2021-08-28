(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn bricks-to-test-for-project [{:keys [name is-run-tests base-names component-names]}
                                  settings
                                  changed-projects
                                  changed-components
                                  changed-bases
                                  project-to-indirect-changes
                                  selected-bricks
                                  is-run-all-brick-tests]
  (let [project-has-changed? (contains? (set changed-projects) name)
        all-brick-names (set (concat (:test base-names) (:test component-names)))
        ;; If the :test key is given for a project in workspace.edn, then only include
        ;; the specified bricks, otherwise, run tests for all bricks that have tests.
        included-bricks (if-let [bricks (get-in settings [:projects name :test :include])]
                          (set/intersection all-brick-names (set bricks))
                          all-brick-names)
        selected-bricks (if selected-bricks
                          (set selected-bricks)
                          all-brick-names)
        changed-bricks (if is-run-tests
                         (if (or is-run-all-brick-tests project-has-changed?)
                           ;; if we pass in :all or if the project has changed (e.g. its configuration)
                           ;; then always run all brick tests.
                           included-bricks
                           (set/intersection included-bricks
                                             selected-bricks
                                             (set (concat changed-components
                                                          changed-bases
                                                          (-> name project-to-indirect-changes :src)
                                                          (-> name project-to-indirect-changes :test)))))
                         #{})
        ;; And finally, if brick:BRICK is given, also filter on that, which means that if we
        ;; pass in both brick:BRICK and :all, we will run the tests for all these bricks,
        ;; whether they have changed or not (directly or indirectly).
        bricks-to-test (set/intersection changed-bricks selected-bricks)]
    [name (-> bricks-to-test sort vec)]))

(defn project-to-bricks-to-test [changed-projects projects settings changed-components changed-bases project-to-indirect-changes selected-bricks is-run-all-brick-tests]
  (into {} (map #(bricks-to-test-for-project % settings changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks is-run-all-brick-tests)
                projects)))
