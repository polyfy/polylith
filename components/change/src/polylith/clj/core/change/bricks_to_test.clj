(ns polylith.clj.core.change.bricks-to-test
  (:require [clojure.set :as set]))

(defn contains-project? [projects project-alias project-name]
  (or (contains? (set projects) project-name)
      (contains? (set projects) project-alias)))

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
  (let [;; If we specify one or more projects with 'project:p1:p2' then only test this
        ;; project's bricks if it's included in that list, even if :all is passed in, see issue 189.

        ;;                         Include all projects except development, but also
        ;;                         include dev if :dev or project:dev is passed in.
        include-project? (and (or (not is-dev)
                                  is-dev-user-input
                                  (contains-project? selected-projects alias name))

        ;;                         And if these statements are true:
        ;;                           - no projects are selected (then include all),
        ;;                             or this project is selected with e.g. project:p1
                              (or (empty? selected-projects)
                                  (contains-project? selected-projects alias name)))
        project-has-changed? (contains? (set changed-projects) name)
        all-brick-names (set (concat (:test base-names) (:test component-names)))
        ;; If the :test key is given for a project in workspace.edn, then only include
        ;; the specified bricks, otherwise, run tests for all bricks that have tests.
        included-bricks (if-let [bricks (get-in settings [:projects name :test :include])]
                          (set/intersection all-brick-names (set bricks))
                          all-brick-names)
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

(defn project-to-bricks-to-test [changed-projects projects settings changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests]
  (into (sorted-map) (map #(bricks-to-test-for-project % settings changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev-user-input is-run-all-brick-tests)
                          projects)))
