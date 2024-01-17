(ns polylith.clj.core.change.bricks-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.bricks-to-test :as to-test]
            [polylith.clj.core.change.test-data :as data])
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
    (into {}
          (mapv (juxt :name :bricks-to-test)
                (map #(to-test/with-bricks-to-test %
                                                   changed-projects
                                                   changed-components
                                                   changed-bases
                                                   selected-bricks
                                                   selected-projects
                                                   is-dev-user-input
                                                   is-run-all-brick-tests)
                     projects)))))

;; The development project is only included in the tests if we pass in :dev,
;; or if we include it with project:dev.

(deftest with-bricks-to-test--with-one-changed-component--returns-bricks-to-test-for-changed-projects
  (is (= {"core" ["article"]
          "development" []}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-one-changed-component-that-is-excluded---returns-bricks-to-test-for-changed-projects
  (is (= {"core" []
          "development" []}
         (test {:changed-projects []
                :test {:include []}
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-run-all-selected--returns-all-bricks
  (is (= {"core" ["article"
                  "comment"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests true}))))

(deftest with-bricks-to-test--with-run-all-brick-tests-and-development-included--returns-all-bricks
  (is (= {"core" ["article"
                  "comment"
                  "rest-api"
                  "tag"
                  "user"]
          "development" ["article"
                         "comment"
                         "profile"
                         "rest-api"
                         "tag"
                         "user"]}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input true
                :is-run-all-brick-tests true}))))

(deftest with-bricks-to-test--with-run-all-brick-tests-and-development-selected--returns-all-bricks-for-development
  (is (= {"core" []
          "development" ["article"
                         "comment"
                         "profile"
                         "rest-api"
                         "tag"
                         "user"]}
         (test {:changed-projects []
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{"dev"}
                :is-dev-user-input false
                :is-run-all-brick-tests true}))))

(deftest with-bricks-to-test--include-two-bricks--returns-the-two-bricks
  (is (= {"core" ["tag"
                  "user"]
          "development" []}
         (test {:changed-projects []
                :test {:include ["tag" "user"]}
                :changed-components ["article" "comment" "rest-api" "tag" "user"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--when-the-project-itself-has-changed--return-all-bricks-for-that-project
  (is (= {"core" ["article"
                  "comment"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []}
         (test {:changed-projects ["core"]
                :changed-components ["article"]
                :changed-bases []
                :selected-bricks nil
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-two-changed-components-and-one-selected-brick--returns-selected-bricks-that-are-also-changed
  (is (=
        {"core" ["user"]
         "development" []}
        (test {:changed-projects []
               :changed-components ["article" "user"]
               :changed-bases []
               :selected-bricks ["user"]
               :selected-projects #{}
               :is-dev-user-input false
               :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-no-changed-components-and-one-selected-brick--returns-no-bricks
  (is (= {"core" []
          "development" []}
         (test {:changed-projects []
                :changed-components []
                :changed-bases []
                :selected-bricks ["tag"]
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests false}))))

(deftest with-bricks-to-test--with-no-changed-components-and-one-selected-brick-with-run-all-brick-tests--returns-selected-brick
  (is (= {"core" ["tag"]
          "development" []}
         (test {:changed-projects []
                :changed-components []
                :changed-bases []
                :selected-bricks ["tag"]
                :selected-projects #{}
                :is-dev-user-input false
                :is-run-all-brick-tests true}))))
