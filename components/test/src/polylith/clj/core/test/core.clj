(ns ^:no-doc polylith.clj.core.test.core
  (:require [polylith.clj.core.test.bricks-to-test :as bricks-to-test]
            [polylith.clj.core.test.projects-to-test :as projects-to-test]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.git.interface :as git]))

(defn nothing-to-test [project]
  (assoc project :bricks-to-test []
                 :projects-to-test []))

(defn with-to-test [{:keys [ws-dir user-input projects paths changes] :as workspace}]
  (if (common/invalid-workspace? workspace)
    workspace
    (if (-> ws-dir git/is-git-repo? not)
      (assoc workspace :projects (mapv nothing-to-test projects))
      (let [{:keys [changed-components changed-bases changed-projects changed-or-affected-projects]} changes
            {:keys [is-dev is-all is-run-all-brick-tests is-run-project-tests selected-bricks selected-projects]} user-input
            projects-with-keys (mapv #(-> %
                                          (bricks-to-test/with-bricks-to-test changed-projects changed-components changed-bases selected-bricks selected-projects is-dev is-run-all-brick-tests)
                                          (projects-to-test/with-projects-to-test paths changed-or-affected-projects selected-projects is-dev is-run-project-tests is-all))
                                     projects)]
        (assoc workspace :projects projects-with-keys)))))
