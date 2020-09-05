(ns polylith.clj.core.change.to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.change.to-test :as to-test]))

(def environments [{:name "development"
                    :dev? true
                    :run-tests? false
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}
                   {:name "core"
                    :dev? false
                    :run-tests? true
                    :has-test-dir? true
                    :component-names ["profile"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}
                   {:name "cli"
                    :dev? false
                    :run-tests? false
                    :has-test-dir? true
                    :component-names ["article"]
                    :test-base-names ["rest-api"]
                    :test-component-names ["article" "comment" "profile" "tag" "user"]}])

(deftest env->bricks-to-test--with-one-changed-brick--returns-bricks-to-test-for-changed-and-active-environments
  (is (= {"cli" []
          "core" ["article"]
          "development" []}
         (to-test/env->bricks-to-test environments ["article"] [] {} false))))

(deftest env->bricks-to-test--with-test-all-selected--returns-all-bricks-for-active-environments
  (is (= {"cli" []
          "core" ["article"
                  "comment"
                  "profile"
                  "rest-api"
                  "tag"
                  "user"]
          "development" []}
         (to-test/env->bricks-to-test environments ["article"] [] {} true))))

(deftest environments-to-test--with-no-changed-bricks--returns-no-environments
  (with-redefs [file/exists (fn [_] true)]
    (is (= []
           (to-test/environments-to-test "." environments [] [] false)))))
