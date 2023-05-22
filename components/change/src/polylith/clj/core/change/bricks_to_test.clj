(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn include-and-exclude-bricks
  "If the :test key is given for a project in workspace.edn, then only include and/or exclude
   the specified bricks, otherwise, run tests for all bricks that have tests."
  [brick-name all-brick-names settings]
  (let [included (if-let [bricks (get-in settings [:projects brick-name :test :include])]
                   (set/intersection all-brick-names (set bricks))
                   all-brick-names)]
    (if-let [bricks (get-in settings [:projects brick-name :test :exclude])]
      (set/difference included (set bricks))
      included)))

(defn bricks-to-test-for-project [{:keys [is-dev alias name base-names component-names]}
                                  settings
                                  changed-projects
                                  changed-components
                                  changed-bases
                                  project-to-indirect-changes
                                  selected-bricks
                                  selected-projects
                                  is-dev-user-input
                                  is-run-all-brick-tests]
  (let [include-project? (or (or (contains? selected-projects name)
                                 (contains? selected-projects alias))
                             (and (empty? selected-projects)
                                  (or (not is-dev)
                                      is-dev-user-input)))
        project-has-changed? (contains? (set changed-projects) name)
        all-brick-names (into #{} (mapcat :test) [base-names component-names])
        included-bricks (include-and-exclude-bricks name all-brick-names settings)
        selected-bricks (if selected-bricks
                          (set selected-bricks)
                          all-brick-names)
        changed-bricks (if include-project?
                         (if (or is-run-all-brick-tests project-has-changed?)
                           ;; if we pass in :all or :all-bricks or if the project has changed
                           ;; then always run all brick tests.
                           included-bricks
                           (set/intersection included-bricks
                                             selected-bricks
                                             (into #{} cat
                                                   [changed-components
                                                    changed-bases
                                                    (-> name project-to-indirect-changes :src)
                                                    (-> name project-to-indirect-changes :test)])))
                         #{})
        ;; And finally, if brick:BRICK is given, also filter on that, which means that if we
        ;; pass in both brick:BRICK and :all, we will run the tests for all these bricks,
        ;; whether they have changed or not (directly or indirectly).
        bricks-to-test (set/intersection changed-bricks selected-bricks)]
    [name (-> bricks-to-test sort vec)]))

(defn project-to-bricks-to-test [changed-projects projects settings changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (into (sorted-map)
        (map #(bricks-to-test-for-project % settings changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests))
        projects))
