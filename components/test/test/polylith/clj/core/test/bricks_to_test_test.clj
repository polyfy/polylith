(ns polylith.clj.core.test.bricks-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test.bricks-to-test :as to-test]
            [polylith.clj.core.test.test-data :as data])
  (:refer-clojure :exclude [test]))

(defn test [{:keys [changed-projects
                    changed-components
                    changed-bases
                    test
                    selected-bricks
                    selected-projects
                    is-dev-user-input
                    is-run-all-brick-tests]}]
  (let [projects (data/projects test)]
    (transduce
      (map #(to-test/with-bricks-to-test %
                                         changed-projects
                                         changed-components
                                         changed-bases
                                         selected-bricks
                                         selected-projects
                                         is-dev-user-input
                                         is-run-all-brick-tests))
      (fn
        ([] {:bricks-to-test {} :bricks-to-test-2 {}})
        ([acc] acc)
        ([acc {:keys [name bricks-to-test bricks-to-test-2]}]
         (-> acc
             (update :bricks-to-test assoc name bricks-to-test)
             (update :bricks-to-test-2 assoc name bricks-to-test-2))))
      projects)))

;; The development project is only included in the tests if we pass in :dev,
;; or if we include it with project:dev.

(deftest with-bricks-to-test--with-three-changed-bricks--returns-bricks-to-test-for-changed-projects
  (is (= {:bricks-to-test
          {"core" ["article" "rest-api"]
           "development" []
           "extension" ["article"]}
          :bricks-to-test-2
          {"core" ["article" "rest-api"]
           "development" []
           "extension" ["article" "cli"]}}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases ["rest-api" "cli"]
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-one-changed-component-that-is-excluded---returns-bricks-to-test-for-changed-projects
  (is (= {:bricks-to-test
          {"core" []
           "development" []
           "extension" []}
          :bricks-to-test-2
          {"core" []
           "development" []
           "extension" []}}
         (test {:changed-projects []
                :test {:include ["foobar"]}
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-run-all-selected--returns-all-bricks
  (is (= {:bricks-to-test
          {"core" ["article"
                   "comment"
                   "rest-api"
                   "tag"
                   "user"]
           "development" []
           "extension" ["article"
                        "comment"]}
          :bricks-to-test-2
          {"core" ["article"
                   "comment"
                   "rest-api"
                   "tag"
                   "user"]
           "development" []
           "extension" ["article"
                        "cli"
                        "comment"
                        "section"]}}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests true}))))

(deftest with-bricks-to-test--with-run-all-brick-tests-and-development-included--returns-all-bricks
  (is (= {:bricks-to-test
          {"core" ["article"
                   "comment"
                   "rest-api"
                   "tag"
                   "user"]
           "development" ["article"
                          "comment"
                          "profile"
                          "rest-api"
                          "tag"
                          "user"]
           "extension" ["article"
                        "comment"]}
          :bricks-to-test-2
          {"core" ["article"
                   "comment"
                   "rest-api"
                   "tag"
                   "user"]
           "development" ["article"
                          "comment"
                          "profile"
                          "rest-api"
                          "tag"
                          "user"]
           "extension" ["article"
                        "cli"
                        "comment"
                        "section"]}}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input true
                :is-run-all-brick-tests true}))))

(deftest with-bricks-to-test--with-run-all-brick-tests-and-development-selected--returns-all-bricks-for-development
  (is (= {:bricks-to-test
          {"core" []
           "development" ["article"
                          "comment"
                          "profile"
                          "rest-api"
                          "tag"
                          "user"]
           "extension" []}
          :bricks-to-test-2
          {"core" []
           "development" ["article"
                          "comment"
                          "profile"
                          "rest-api"
                          "tag"
                          "user"]
           "extension" []}}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{"dev"}
                :is-dev-user-input false
                :is-run-all-brick-tests true}))))

(deftest with-bricks-to-test--include-two-bricks--returns-the-two-bricks
  (is (= {:bricks-to-test
          {"core" ["tag"
                   "user"]
           "development" []
           "extension" []}
          :bricks-to-test-2
          {"core" ["tag"
                   "user"]
           "development" []
           "extension" []}}
         (test {:changed-projects []
                :test {:include ["tag" "user"]}
                :changed-components ["article" "comment" "tag" "user"]
                :changed-bases ["rest-api"]
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--when-the-project-itself-has-changed--return-all-bricks-for-that-project
  (is (= {:bricks-to-test
          {"core" ["article"
                   "comment"
                   "rest-api"
                   "tag"
                   "user"]
           "development" []
           "extension" ["article"]}
          :bricks-to-test-2
          {"core" ["article"
                   "comment"
                   "rest-api"
                   "tag"
                   "user"]
           "development" []
           "extension" ["article"
                        "cli"]}}
         (test {:changed-projects ["core"]
                :changed-components ["article"]
                :changed-bases ["cli"]
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-two-changed-components-and-one-selected-brick--returns-selected-bricks-that-are-also-changed
  (is (= {:bricks-to-test
          {"core" ["user"]
           "development" []
           "extension" []}
          :bricks-to-test-2
          {"core" ["user"]
           "development" []
           "extension" []}}
         (test {:changed-projects []
               :changed-components ["article" "user"]
               :changed-bases []
               :selected-bricks ["user"]
               :selected-projects #{}
               :is-dev-user-input false
               :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-no-changed-components-and-one-selected-brick--returns-no-bricks
  (is (= {:bricks-to-test
          {"core" []
           "development" []
           "extension" []}
          :bricks-to-test-2
          {"core" []
           "development" []
           "extension" []}}
         (test {:changed-projects []
                :changed-components []
                :changed-bases []
                :selected-bricks ["tag"]
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-no-changed-components-and-one-selected-brick-with-run-all-brick-tests--returns-selected-brick
  (is (= {:bricks-to-test
          {"core" ["tag"]
           "development" []
           "extension" []}
          :bricks-to-test-2
          {"core" ["tag"]
           "development" []
           "extension" []}}
         (test {:changed-projects []
                :changed-components []
                :changed-bases []
                :selected-bricks ["tag"]
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests true})))

  (is (= {:bricks-to-test
          {"core" []
           "development" []
           "extension" []}
          :bricks-to-test-2
          {"core" []
           "development" []
           "extension" ["section"]}}
         (test {:changed-projects []
                :changed-components []
                :changed-bases []
                :selected-bricks ["section"]
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests true}))))
