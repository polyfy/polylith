(ns polylith.clj.core.change.bricks-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.bricks-to-test :as to-test]
            [polylith.clj.core.change.projects-test-data :as data]))

(deftest project-to-bricks-to-test--with-one-changed-brick--returns-bricks-to-test-for-changed-and-active-projects
  (is (= {"cli" []
          "core" ["article"]
          "development" []}
         (to-test/project-to-bricks-to-test {} data/projects {} ["article"] [] {} false))))

(deftest project-to-bricks-to-test--with-one-changed-brick-that-is-excluded---returns-bricks-to-test-for-changed-and-active-projects
  (is (= {"cli" []
          "core" []
          "development" []}
         (to-test/project-to-bricks-to-test {}
                                            data/projects
                                            {:projects {"core" {:test []}}}
                                            ["article"] [] {} false))))

(deftest project-to-bricks-to-test--with-test-all-selected--returns-all-bricks-for-active-projects
  (is (= {"cli" []
          "core" ["article"
                  "comment"
                  "profile"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []}
         (to-test/project-to-bricks-to-test {} data/projects {} ["article"] [] {} true))))

(deftest project-to-bricks-to-test--with-test-all-selected-only-test-two-bricks--returns-the-two-bricks-for-active-projects
  (is (= {"cli" []
          "core" ["tag"
                  "user"]
          "development" []}
         (to-test/project-to-bricks-to-test {} data/projects
                                            {:projects {"core" {:test ["tag" "user"]}}}
                                            ["article"] [] {} true))))

(deftest project-to-bricks-to-test--when-the-project-itself-has-changed--return-all-bricks-for-that-project
  (is (= {"cli" []
          "core" ["article"
                  "comment"
                  "profile"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []}
         (to-test/project-to-bricks-to-test ["core"] data/projects {} ["article"] [] {} false))))
