(ns polylith.clj.core.change.core
  (:require [clojure.set :as set]
            [polylith.clj.core.change.entity :as entity]
            [polylith.clj.core.change.indirect :as indirect]
            [polylith.clj.core.change.bricks-to-test :as bricks-to-test]
            [polylith.clj.core.change.projects-to-test :as projects-to-test]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]))

(defn project-affected? [{:keys [name component-names base-names]}
                         changed-components changed-bases changed-projects]
  (let [bricks (set (concat component-names base-names))
        changed-bricks (set (concat changed-components changed-bases))
        brick-changed? (-> (set/intersection bricks changed-bricks)
                           empty? not)
        project-changed? (contains? (set changed-projects) name)]
    (or brick-changed? project-changed?)))

(defn affected-projects [projects changed-components changed-bases changed-projects]
  (vec (sort (map :name (filter #(project-affected? % changed-components changed-bases changed-projects)
                                projects)))))

(defn changes [{:keys [projects settings paths user-input]}
               {:keys [since since-sha tag files]}]
   (let [deps (map (juxt :name :deps) projects)
         {:keys [is-dev is-all is-run-all-brick-tests is-run-project-tests]} user-input
         {:keys [changed-components
                 changed-bases
                 changed-projects]} (entity/changed-entities files nil)
         changed-bricks (set (concat changed-components changed-bases))
         affected-projects (affected-projects projects changed-components changed-bases changed-projects)
         project-to-indirect-changes (indirect/project-to-indirect-changes deps changed-bricks)
         project-to-bricks-to-test (bricks-to-test/project-to-bricks-to-test changed-projects projects settings changed-components changed-bases project-to-indirect-changes is-run-all-brick-tests)
         project-to-projects-to-test (projects-to-test/project-to-projects-to-test projects affected-projects paths is-dev is-run-project-tests is-all)]
     (util/ordered-map :since since
                       :since-sha since-sha
                       :since-tag tag
                       :git-command (git/diff-command since-sha nil)
                       :changed-components changed-components
                       :changed-bases changed-bases
                       :changed-projects changed-projects
                       :changed-or-affected-projects affected-projects
                       :project-to-indirect-changes project-to-indirect-changes
                       :project-to-bricks-to-test project-to-bricks-to-test
                       :project-to-projects-to-test project-to-projects-to-test
                       :changed-files files)))

(defn find-sha [ws-dir since {:keys [release-tag-pattern stable-tag-pattern]}]
  (case since
    "release" (git/release ws-dir release-tag-pattern false)
    "previous-release" (git/release ws-dir release-tag-pattern true)
    (git/latest-stable ws-dir stable-tag-pattern)))

(defn with-changes
  ([{:keys [ws-dir settings user-input] :as workspace}]
   (if (-> ws-dir git/is-git-repo? not)
     workspace
     (let [since (:since user-input "stable")
           {:keys [tag sha]} (find-sha ws-dir since settings)]
       (assoc workspace :changes
                        (changes workspace {:tag tag
                                            :since since
                                            :since-sha sha
                                            :files (git/diff ws-dir sha nil)}))))))
