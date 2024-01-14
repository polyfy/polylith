(ns ^:no-doc polylith.clj.core.change.core
  (:require [clojure.set :as set]
            [polylith.clj.core.change.entity :as entity]
            [polylith.clj.core.change.indirect :as indirect]
            [polylith.clj.core.change.bricks-to-test :as bricks-to-test]
            [polylith.clj.core.change.projects-to-test :as projects-to-test]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]))

(defn project-affected? [{:keys [name component-names base-names]}
                         changed-components changed-bases changed-projects]
  (let [bricks (set (concat (:src component-names) (:src base-names)))
        changed-bricks (set (concat changed-components changed-bases))
        brick-changed? (-> (set/intersection bricks changed-bricks)
                           empty? not)
        project-changed? (contains? (set changed-projects) name)]
    (or brick-changed? project-changed?)))

(defn affected-projects [projects changed-components changed-bases changed-projects]
  (vec (sort (map :name (filter #(project-affected? % changed-components changed-bases changed-projects)
                                projects)))))

(defn nothing-to-test [project]
  (assoc project :bricks-to-test []
                 :projects-to-test []))

(defn with-changes [{:keys [ws-dir ws-local-dir user-input settings projects paths] :as workspace}]
  (if (common/invalid-workspace? workspace)
    workspace
    (if (-> ws-dir git/is-git-repo? not)
      (assoc workspace :changes {:changed-files []
                                 :changed-components []
                                 :changed-bases []
                                 :changed-projects []
                                 :changed-or-affected-projects []
                                 :project-to-indirect-changes {}}
                       :projects (mapv nothing-to-test projects))
      (let [{:keys [since changed-files is-no-changes]
             :or {since "stable"}} user-input
            tag-patterns (:tag-patterns settings)
            {:keys [tag sha]} (git/sha ws-dir since tag-patterns)
            files (git/diff ws-dir ws-local-dir is-no-changes changed-files sha nil)]
        (let [projects-deps (mapv (juxt :name :deps) projects)
              {:keys [is-dev is-all is-run-all-brick-tests is-run-project-tests selected-bricks selected-projects]} user-input
              {:keys [changed-components
                      changed-bases
                      changed-projects]} (entity/changed-entities files paths)
              changed-bricks (set (concat changed-components changed-bases))
              affected-projects (affected-projects projects changed-components changed-bases changed-projects)
              project-to-indirect-changes (indirect/project-to-indirect-changes projects-deps changed-bricks)
              projects-with-to-test (-> projects
                                        (bricks-to-test/with-bricks-to-test changed-projects changed-components changed-bases project-to-indirect-changes selected-bricks selected-projects is-dev is-run-all-brick-tests)
                                        (projects-to-test/with-projects-to-test paths affected-projects selected-projects is-dev is-run-project-tests is-all))
              changes (util/ordered-map :since since
                                        :since-sha sha
                                        :since-tag tag
                                        :changed-files files
                                        :git-diff-command (git/diff-command sha nil)
                                        :changed-components changed-components
                                        :changed-bases changed-bases
                                        :changed-projects changed-projects
                                        :changed-or-affected-projects affected-projects
                                        :project-to-indirect-changes project-to-indirect-changes)]
          (assoc workspace :changes changes
                           :projects projects-with-to-test))))))
