(ns ^:no-doc polylith.clj.core.test.bricks-to-test
  (:require [clojure.set :as set]
            [polylith.clj.core.common.interface :as common]))

(defn bricks-for-source [{:keys [base-names component-names]} source]
  (if (= :test source)
    ;; Make sure we only select bricks that are included in the project from the :test context.
    (into #{} (mapcat :test) [base-names component-names])
    ;; Also make sure the bricks are included in the :src context for the project
    ;; when checking for changes for the :src context.
    ;; If a brick is included in the project via its deps.edn as :local/root
    ;; from :aliases > :test > :extra-deps, then it will only be included in :test
    ;; in base-names/component-names (not :src).
    (set/intersection (into #{} (mapcat :src) [base-names component-names])
                      (into #{} (mapcat :test) [base-names component-names]))))

(defn bricks-referenced [{:keys [base-names component-names]}]
  (into #{} (comp (mapcat vals) cat) [base-names component-names]))

(defn include-project? [{:keys [is-dev alias name]} selected-projects is-dev-user-input]
  (or (or (contains? selected-projects name)
          (contains? selected-projects alias))
      (and (empty? selected-projects)
           (or (not is-dev)
               is-dev-user-input))))

(defn project-changed? [{:keys [name]} changed-projects]
  (contains? (set changed-projects) name))

(defn bricks-to-test [{:keys [test indirect-changes]}
                      source
                      changed-bricks
                      user-selected-bricks
                      include-project
                      project-has-changed
                      is-run-all-brick-tests
                      eligible-bricks]
  (let [;; If the :test > :include or :test > :exclude keys are given for a project in workspace.edn,
        ;; then only include the specified bricks, otherwise, run tests for all bricks.
        included-bricks (common/brick-names-to-test test eligible-bricks)
        user-selected-bricks (if user-selected-bricks (set user-selected-bricks) eligible-bricks)
        selected-bricks (if include-project
                          (if (or is-run-all-brick-tests project-has-changed)
                            ;; if we pass in :all or :all-bricks or if the project has changed then always run all brick tests.
                            included-bricks
                            (set/intersection included-bricks
                                              user-selected-bricks
                                              (into #{} cat
                                                    [changed-bricks (source indirect-changes)])))
                          #{})]
    ;; And finally, if brick:BRICK is given, also filter on that, which means that if we
    ;; pass in both brick:BRICK and :all, we will run the tests for all these bricks,
    ;; whether they have changed or not (directly or indirectly).
    (set/intersection selected-bricks user-selected-bricks)))

(defn with-bricks-to-test [project changed-projects changed-components changed-bases user-selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (let [changed-bricks (into [] cat [changed-bases changed-components])
        include-project (include-project? project selected-projects is-dev-user-input)
        project-has-changed (project-changed? project changed-projects)]
    (assoc project
      ;; only ever includes bricks that have test sources
      :bricks-to-test
      (->> [:src :test]
           (into
             (sorted-set)
             (mapcat #(bricks-to-test project
                                      %
                                      changed-bricks
                                      user-selected-bricks
                                      include-project
                                      project-has-changed
                                      is-run-all-brick-tests
                                      (bricks-for-source project %))))
           (vec))
      ;; might include bricks that don't have test sources
      :bricks-to-test-2
      (->> [:src :test]
           (into
             (sorted-set)
             (mapcat #(bricks-to-test project
                                      %
                                      changed-bricks
                                      user-selected-bricks
                                      include-project
                                      project-has-changed
                                      is-run-all-brick-tests
                                      (bricks-referenced project))))
           (vec)))))
