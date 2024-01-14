(ns polylith.clj.core.change.projects-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.projects-to-test :as t]))

(deftest run-tests?--dont-run-project-tests-by-default
  (is (false?
        (t/run-tests? "s" "service" false false false #{}))))

(deftest run-tests?--dont-run-dev-project-tests-by-default
  (is (false?
        (t/run-tests? "dev" "development" true true false #{}))))

(deftest run-tests?--run-project-tests-if-project-or-all-argument-is-passed-in
  (is (true?
        (t/run-tests? "s" "service" false false true #{}))))

(deftest run-tests?--run-project-tests-if-project-alias-is-selected-but-projects-are-not-marked-for-testing
  (is (false?
        (t/run-tests? "s" "service" false false false #{"s"}))))

(deftest run-tests?--run-project-tests-if-project-alias-is-selected
  (is (true?
        (t/run-tests? "s" "service" false false true #{"s"}))))

(deftest run-tests?--run-project-tests-if-project-name-is-selected-but-projects-are-not-marked-for-testing
  (is (false?
        (t/run-tests? "s" "service" false false false #{"service"}))))

(deftest run-tests?--run-project-tests-if-project-name-is-selected
  (is (true?
        (t/run-tests? "s" "service" false false true #{"service"}))))

(deftest run-tests?--dont-run-dev-project-tests-if-project-alias-is-selected-but-projects-are-not-marked-for-testing
  (is (false?
        (t/run-tests? "dev" "development" true false false #{"dev"}))))

(deftest run-tests?--run-dev-project-tests-if-project-alias-is-selected
  (is (true?
        (t/run-tests? "dev" "development" true false true #{"dev"}))))

(deftest run-tests?--dont-run-dev-project-tests-if-project-name-is-selected-but-projects-are-not-marked-for-testing
  (is (false?
        (t/run-tests? "dev" "development" true false false #{"development"}))))

(deftest run-tests?--run-dev-project-tests-if-project-name-is-selected
  (is (true?
        (t/run-tests? "dev" "development" true false true #{"development"}))))

(deftest run-tests?--dont-run-project-tests-if-selected-projects-only-contains-other-projects
  (is (false?
        (t/run-tests? "s" "service" false false true #{"another"}))))

(deftest run-tests?--run-dev-project-tests-if-run-project-test-flag-is-set
  (is (true?
        (t/run-tests? "dev" "development" true true true #{}))))

(defn with-to-test [project disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all]
  (:projects-to-test (t/with-to-test project disk-paths affected-projects selected-projects is-dev-user-input is-run-project-tests is-all)))

(deftest dont-run-project-tests--project-tests-are-not-activated-by-default
  (is (= []
         (with-to-test {:is-dev false
                        :name "service"
                        :paths {:test ["projects/service/test"]}}
                       ["projects/service/test"] ; disk-paths
                       ["service"] ; affected-projects
                       #{} ; selected-projects
                       false ; is-dev-user-input
                       false ; is-run-project-tests
                       false)))) ; is-all

(deftest not-dev--run-affected-projects
  (is (= ["service"]
         (with-to-test {:is-dev false
                        :name "service"
                        :paths {:test ["projects/service/test"]}}
                       ["projects/service/test"] ; disk-paths
                       ["service"] ; affected-projects
                       #{} ; selected-projects
                       false ; is-dev-user-input
                       true ; is-run-project-tests
                       false)))) ; is-all

(deftest dev--dont-run-affected-projects--development-is-excluded-by-default
  (is (= []
         (with-to-test {:is-dev true
                        :name "development"
                        :paths {:test ["projects/service/test"]}}
                       ["projects/service/test"] ; disk-paths
                       ["service"] ; affected-projects
                       #{} ; selected-projects
                       false ; is-dev-user-input
                       true ; is-run-project-tests
                       false)))) ; is-all

(deftest dev--run-affected-projects--dev-is-active
  (is (= ["service"]
         (with-to-test {:is-dev true
                        :alias "dev"
                        :name "development"
                        :paths {:test ["projects/service/test"]}}
                       ["projects/service/test"] ; disk-paths
                       ["service"] ; affected-projects
                       #{"dev"} ; selected-projects
                       true ; is-dev-user-input
                       true ; is-run-project-tests
                       false)))) ; is-all

(deftest not-dev--run-all-tests
  (is (= ["service"]
         (with-to-test {:is-dev false
                        :name "service"
                        :paths {:test ["projects/service/test"]}}
                       ["projects/service/test"] ; disk-paths
                       [] ; affected-projects
                       #{} ; selected-projects
                       false ; is-dev-user-input
                       true ; is-run-project-tests
                       true)))) ; is-all
