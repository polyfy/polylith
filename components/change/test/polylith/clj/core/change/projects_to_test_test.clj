(ns polylith.clj.core.change.projects-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.projects-test-data :as data]
            [polylith.clj.core.change.projects-to-test :as to-test]))

(deftest projects-to-test--with-no-changed-bricks--returns-no-projects
  (is (= {"cli" []
          "core" []
          "development" []}
         (to-test/project-to-projects-to-test data/projects [] [] false false false))))

(deftest projects-to-test--where-project-is-affected-and-contains-project-test-path--returns-affected-project
  (is (= {"cli" []
          "core" ["core"]
          "development" []}
         (to-test/project-to-projects-to-test data/projects
                                              ["core"]
                                              {:existing ["projects/core/test"]}
                                              false true false))))
