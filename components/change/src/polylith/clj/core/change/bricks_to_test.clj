(ns ^:no-doc polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn bricks-to-test [{:keys [is-dev alias name test base-names component-names]}
                      source
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
        included-bricks (common/brick-names-to-test test bricks-for-source)
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
                         #{})]
    ;; And finally, if brick:BRICK is given, also filter on that, which means that if we
    ;; pass in both brick:BRICK and :all, we will run the tests for all these bricks,
    ;; whether they have changed or not (directly or indirectly).
    (set/intersection changed-bricks selected-bricks)))

(defn with-to-test [project changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (let [res1 (bricks-to-test project :src changed-projects changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
        res2 (bricks-to-test project :test changed-projects changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)])
  (assoc project
         :bricks-to-test
         (-> (concat (bricks-to-test project :src changed-projects changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
                     (bricks-to-test project :src changed-projects changed-components project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
                     (bricks-to-test project :test changed-projects changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
                     (bricks-to-test project :test changed-projects changed-components project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests))
             set sort vec)))

(defn with-bricks-to-test [projects changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (mapv #(with-to-test % changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
        projects))
