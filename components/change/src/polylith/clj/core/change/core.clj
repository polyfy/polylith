(ns polylith.clj.core.change.core
  (:require [polylith.clj.core.change.entity :as entity]
            [polylith.clj.core.change.indirect :as indirect]
            [polylith.clj.core.change.to-test :as to-test]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.git.interfc :as git]
            [polylith.clj.core.util.interfc :as util]))

(defn changes [{:keys [ws-dir environments user-input]}
               {:keys [sha1 sha2 files]}]
   (let [deps (map (juxt :name :deps) environments)
         {:keys [run-all? run-env-tests?]} user-input
         {:keys [changed-components
                 changed-bases
                 changed-environments]} (entity/changed-entities ws-dir files)
         changed-bricks (set (concat changed-components changed-bases))
         env->indirect-changes (indirect/env->indirect-changes deps changed-bricks)
         env->bricks-to-test (to-test/env->bricks-to-test environments changed-components changed-bases env->indirect-changes run-all?)
         environments-to-test (to-test/environments-to-test ws-dir environments changed-bricks changed-environments run-env-tests?)]
     (util/ordered-map :sha1 sha1
                       :sha2 sha2
                       :git-command (git/diff-command sha1 sha2)
                       :changed-components changed-components
                       :changed-bases changed-bases
                       :changed-environments changed-environments
                       :env->indirect-changes env->indirect-changes
                       :env->bricks-to-test env->bricks-to-test
                       :environments-to-test environments-to-test
                       :changed-files files)))

(defn with-changes [{:keys [ws-dir settings] :as workspace}]
  (if (-> ws-dir git/is-git-repo? not)
    workspace
    (let [{:keys [stable-since-tag-pattern]} settings
          {:keys [sha]} (git/latest-stable ws-dir stable-since-tag-pattern)]
      (assoc workspace :changes
                       (changes workspace {:sha1 sha
                                           :files (git/diff ws-dir sha nil)})))))
