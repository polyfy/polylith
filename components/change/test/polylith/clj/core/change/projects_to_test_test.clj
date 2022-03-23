(ns polylith.clj.core.change.projects-to-test-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.projects-to-test :as t]))

(deftest dont-run-project-tests--project-tests-are-not-activated-by-default
  (is (= ["service" []]
         (t/projects-to-test {:is-dev false
                              :name "service"
                              :paths {:test ["projects/service/test"]}}
                             ["projects/service/test"] ; disk-paths
                             ["service"] ; affected-projects
                             #{} ; selected-projects
                             false ; is-dev-user-input
                             false ; is-run-project-tests
                             false)))) ; is-all))))

(deftest not-dev--run-affected-projects
  (is (= ["service" ["service"]]
         (t/projects-to-test {:is-dev false
                              :name "service"
                              :paths {:test ["projects/service/test"]}}
                             ["projects/service/test"] ; disk-paths
                             ["service"] ; affected-projects
                             #{} ; selected-projects
                             false ; is-dev-user-input
                             true ; is-run-project-tests
                             false)))) ; is-all))))

(deftest dev--dont-run-affected-projects--development-is-excluded-by-default
  (is (= ["development" []]
         (t/projects-to-test {:is-dev true
                              :name "development"
                              :paths {:test ["projects/service/test"]}}
                             ["projects/service/test"] ; disk-paths
                             ["service"] ; affected-projects
                             #{} ; selected-projects
                             false ; is-dev-user-input
                             true ; is-run-project-tests
                             false)))) ; is-all))))

(deftest dev--run-affected-projects--dev-is-active
  (is (= ["development" ["service"]]
         (t/projects-to-test {:is-dev true
                              :alias "dev"
                              :name "development"
                              :paths {:test ["projects/service/test"]}}
                             ["projects/service/test"] ; disk-paths
                             ["service"] ; affected-projects
                             #{"dev"} ; selected-projects
                             true ; is-dev-user-input
                             true ; is-run-project-tests
                             false)))) ; is-all))))

(deftest not-dev--run-all-tests
  (is (= ["service" ["service"]]
         (t/projects-to-test {:is-dev false
                              :name "service"
                              :paths {:test ["projects/service/test"]}}
                             ["projects/service/test"] ; disk-paths
                             [] ; affected-projects
                             #{} ; selected-projects
                             false ; is-dev-user-input
                             true ; is-run-project-tests
                             true)))) ; is-all))))
