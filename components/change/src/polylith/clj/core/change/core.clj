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
               {:keys [since-sha tag files]}]
   (let [deps (map (juxt :name :deps) environments)
         {:keys [is-dev is-run-all-tests is-run-all-brick-tests run-env-tests?]} user-input
         {:keys [changed-components
                 changed-bases
                 changed-environments]} (entity/changed-entities files nil)
         changed-bricks (set (concat changed-components changed-bases))
         affected-envs (affected-environments environments changed-components changed-bases changed-environments)
         env->indirect-changes (indirect/env->indirect-changes deps changed-bricks)
         env->bricks-to-test (bricks-to-test/env->bricks-to-test changed-environments environments changed-components changed-bases env->indirect-changes is-run-all-brick-tests)
         env->environments-to-test (envs-to-test/env->environments-to-test environments changed-environments paths is-dev run-env-tests? is-run-all-tests)]
     (util/ordered-map :since-sha since-sha
                       :tag tag
                       :git-command (git/diff-command since-sha nil)
                       :changed-components changed-components
                       :changed-bases changed-bases
                       :changed-environments changed-environments
                       :changed-or-affected-environments affected-envs
                       :env->indirect-changes env->indirect-changes
                       :env->bricks-to-test env->bricks-to-test
                       :env->environments-to-test env->environments-to-test
                       :changed-files files)))

(defn find-sha [ws-dir {:keys [changes-since build-tag-pattern stable-since-tag-pattern]}]
  (if (= "previous-build" changes-since)
    (git/previous-build ws-dir build-tag-pattern)
    (git/latest-stable ws-dir stable-since-tag-pattern)))

(defn with-changes
  ([{:keys [ws-dir settings] :as workspace}]
   (if (-> ws-dir git/is-git-repo? not)
     workspace
     (let [{:keys [tag sha]} (find-sha ws-dir settings)]
       (assoc workspace :changes
                        (changes workspace {:tag tag
                                            :since-sha sha
                                            :files (git/diff ws-dir sha nil)}))))))
