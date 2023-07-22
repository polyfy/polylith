(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn bricks-to-test-for-project [{:keys [is-dev alias name base-names component-names]}
                                  source
                                  settings
                                  changed-projects
                                  changed-bricks-for-source
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
        bricks-for-source (if (= :test source)
                            ;; Make sure we only select bricks that are included in the project from the :test context.
                            (into #{} (mapcat :test) [base-names component-names])
                            ;; Also make sure the bricks are included in the :src context for the project
                            ;; when checking for changes for the :src context.
                            ;; If a brick is included in the project via its deps.edn as :local/root
                            ;; from :aliases > :test > :extra-deps, then it will only be included in :test
                            ;; in base-names/component-names (not :src).
                            (set/intersection (into #{} (mapcat :src) [base-names component-names])
                                              (into #{} (mapcat :test) [base-names component-names])))
        ;; If the :test > :include or :test > :exclude keys are given for a project in workspace.edn,
        ;; then only include the specified bricks, otherwise, run tests for all bricks.
        included-bricks (common/brick-names-to-test settings name bricks-for-source)
        selected-bricks (if selected-bricks
                          (set selected-bricks)
                          bricks-for-source)
        changed-bricks (if include-project?
                         (if (or is-run-all-brick-tests project-has-changed?)
                           ;; if we pass in :all or :all-bricks or if the project has changed then always run all brick tests.
                           included-bricks
                           (set/intersection included-bricks
                                             selected-bricks
                                             (into #{} cat
                                                   [changed-bricks-for-source
                                                    (-> name project-to-indirect-changes source)])))
                         #{})
        ;; And finally, if brick:BRICK is given, also filter on that, which means that if we
        ;; pass in both brick:BRICK and :all, we will run the tests for all these bricks,
        ;; whether they have changed or not (directly or indirectly).
        bricks-to-test (set/intersection changed-bricks selected-bricks)]
    [name (-> bricks-to-test sort set)]))

(defn project-to-bricks-to-test-for-source [source changed-projects projects settings changed-bricks project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (into (sorted-map)
        (map #(bricks-to-test-for-project % source settings changed-projects changed-bricks project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests))
        projects))

(defn as-vec-val [[k v]]
  [k (-> v sort vec)])

(defn as-vec [m]
  (into {} (map as-vec-val m)))

(defn project-to-bricks-to-test [changed-projects projects settings changed-bricks-src changed-bricks-test project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (as-vec (merge-with into (project-to-bricks-to-test-for-source :src changed-projects projects settings changed-bricks-src project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
                           (project-to-bricks-to-test-for-source :test changed-projects projects settings changed-bricks-test project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests))))
