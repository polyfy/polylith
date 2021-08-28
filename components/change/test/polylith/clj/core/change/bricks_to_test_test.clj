(ns polylith.clj.core.change.bricks-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.bricks-to-test :as to-test]
            [polylith.clj.core.change.test-data :as data]))

(defn test [{:keys [changed-projects
                    settings
                    changed-components
                    changed-bases
                    project-to-indirect-changes
                    selected-bricks
                    is-run-all-brick-tests]}]
  (to-test/project-to-bricks-to-test changed-projects
                                     data/projects
                                     settings
                                     changed-components
                                     changed-bases
                                     project-to-indirect-changes
                                     selected-bricks
                                     is-run-all-brick-tests))

(deftest project-to-bricks-to-test--with-one-changed-component--returns-bricks-to-test-for-changed-and-active-projects
  (is (= {"cli" []
          "core" ["article"]
          "development" []}
         (test {:changed-projects []
                :settings {}
                :changed-components ["article"]
                :changed-bases []
                :project-to-indirect-changes {}
                :selected-bricks nil
                :is-run-all-brick-tests false}))))

(deftest project-to-bricks-to-test--with-one-changed-component-that-is-excluded---returns-bricks-to-test-for-changed-and-active-projects
  (is (= {"cli" []
          "core" []
          "development" []}
         (test {:changed-projects []
                :settings {:projects {"core" {:test {:include []}}}}
                :changed-components ["article"]
                :changed-bases []
                :project-to-indirect-changes {}
                :selected-bricks nil
                :is-run-all-brick-tests false}))))

(deftest project-to-bricks-to-test--with-run-all-selected--returns-all-bricks-for-active-projects
  (is (= {"cli" []
          "core" ["article"
                  "comment"
                  "profile"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []})
    (test {:changed-projects []
           :settings {}
           :changed-components ["article"]
           :changed-bases []
           :project-to-indirect-changes {}
           :selected-bricks nil
           :is-run-all-brick-tests true})))

(deftest project-to-bricks-to-test--with-run-all-selected-only-test-two-bricks--returns-the-two-bricks-for-active-projects
  (is (= {"cli" []
          "core" ["tag"
                  "user"]
          "development" []})
    (test {:changed-projects []
           :settings {:projects {"core" {:test {:include ["tag" "user"]}}}}
           :changed-components ["article"]
           :changed-bases []
           :project-to-indirect-changes {}
           :selected-bricks nil
           :is-run-all-brick-tests false})))

(deftest project-to-bricks-to-test--when-the-project-itself-has-changed--return-all-bricks-for-that-project
  (is (= {"cli" []
          "core" ["article"
                  "comment"
                  "profile"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []})
    (test {:changed-projects ["core"]
           :settings {}
           :changed-components ["article"]
           :changed-bases []
           :project-to-indirect-changes {}
           :selected-bricks nil
           :is-run-all-brick-tests false})))

(deftest project-to-bricks-to-test--with-two-changed-components-and-one-selected-brick--returns-selected-bricks-that-are-also-changed
  (is (= {"cli" []
          "core" ["user"]
          "development" []}
         (test {:changed-projects []
                :settings {}
                :changed-components ["article" "user"]
                :changed-bases []
                :project-to-indirect-changes {}
                :selected-bricks ["user"]
                :is-run-all-brick-tests false}))))

(deftest project-to-bricks-to-test--with-no-changed-components-and-one-selected-brick--returns-no-bricks
  (is (= {"cli" []
          "core" []
          "development" []}
         (test {:changed-projects []
                :settings {}
                :changed-components []
                :changed-bases []
                :project-to-indirect-changes {}
                :selected-bricks ["tag"]
                :is-run-all-brick-tests false}))))

(deftest project-to-bricks-to-test--with-no-changed-components-and-one-selected-brick-with-run-all-selected--returns-selected-brick
  (is (= {"cli" []
          "core" ["tag"]
          "development" []}
         (test {:changed-projects []
                :settings {}
                :changed-components []
                :changed-bases []
                :project-to-indirect-changes {}
                :selected-bricks ["tag"]
                :is-run-all-brick-tests true}))))
