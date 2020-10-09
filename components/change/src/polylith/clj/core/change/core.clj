(ns polylith.clj.core.change.core
  (:require [clojure.set :as set]
            [polylith.clj.core.change.entity :as entity]
            [polylith.clj.core.change.indirect :as indirect]
            [polylith.clj.core.change.bricks-to-test :as bricks-to-test]
            [polylith.clj.core.change.environments-to-test :as envs-to-test]
            [polylith.clj.core.git.interface :as git]
            [polylith.clj.core.util.interface :as util]))

(defn env-affected? [{:keys [name component-names base-names]}
                     changed-components changed-bases changed-environments]
  (let [bricks (set (concat component-names base-names))
        changed-bricks (set (concat changed-components changed-bases))
        brick-changed? (-> (set/intersection bricks changed-bricks)
                           empty? not)
        environment-changed? (contains? (set changed-environments) name)]
    (or brick-changed? environment-changed?)))

(defn affected-environments [environments changed-components changed-bases changed-environments]
  (vec (sort (map :name (filter #(env-affected? % changed-components changed-bases changed-environments)
                                environments)))))

(defn changes [{:keys [environments paths user-input]}
               {:keys [since since-sha tag files]}]
   (let [deps (map (juxt :name :deps) environments)
         {:keys [is-dev is-all is-run-all-brick-tests is-run-env-tests]} user-input
         {:keys [changed-components
                 changed-bases
                 changed-environments]} (entity/changed-entities files nil)
         changed-bricks (set (concat changed-components changed-bases))
         affected-envs (affected-environments environments changed-components changed-bases changed-environments)
         env-to-indirect-changes (indirect/env-to-indirect-changes deps changed-bricks)
         env-to-bricks-to-test (bricks-to-test/env-to-bricks-to-test changed-environments environments changed-components changed-bases env-to-indirect-changes is-run-all-brick-tests)
         env-to-environments-to-test (envs-to-test/env-to-environments-to-test environments changed-environments paths is-dev is-run-env-tests is-all)]
     (util/ordered-map :since since
                       :since-sha since-sha
                       :since-tag tag
                       :git-command (git/diff-command since-sha nil)
                       :changed-components changed-components
                       :changed-bases changed-bases
                       :changed-environments changed-environments
                       :changed-or-affected-environments affected-envs
                       :env-to-indirect-changes env-to-indirect-changes
                       :env-to-bricks-to-test env-to-bricks-to-test
                       :env-to-environments-to-test env-to-environments-to-test
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
     (let [since (:since user-input "last-stable")
           {:keys [tag sha]} (find-sha ws-dir since settings)]
       (assoc workspace :changes
                        (changes workspace {:tag tag
                                            :since since
                                            :since-sha sha
                                            :files (git/diff ws-dir sha nil)}))))))
