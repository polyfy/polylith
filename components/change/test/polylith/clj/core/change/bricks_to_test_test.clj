(ns polylith.clj.core.change.bricks-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.bricks-to-test :as to-test]
            [polylith.clj.core.change.environments-test-data :as data]))

(deftest env->bricks-to-test--with-one-changed-brick--returns-bricks-to-test-for-changed-and-active-environments
  (is (= {"cli" []
          "core" ["article"]
          "development" []}
         (to-test/env->bricks-to-test data/environments ["article"] [] {} false))))

(deftest env->bricks-to-test--with-test-all-selected--returns-all-bricks-for-active-environments
  (is (= {"cli" []
          "core" ["article"
                  "comment"
                  "profile"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []}
         (to-test/env->bricks-to-test data/environments ["article"] [] {} true))))
