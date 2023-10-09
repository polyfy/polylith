(ns polylith.clj.core.user-input.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn test-arglist [& args]
  (select-keys (user-input/extract-arguments args)
               [:is-all
                :is-run-all-brick-tests
                :is-run-project-tests
                :selected-projects]))

(defn active-profiles-params [& args]
  (select-keys (user-input/extract-arguments args)
               [:selected-profiles]))

(deftest arguments--no-arguments
  (is (= (test-arglist)
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{}})))

(deftest arguments--a-single-project
  (is (= (test-arglist "cmd" "project:core")
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"core"}})))

(deftest arguments--a-list-of-projects
  (is (= (test-arglist "cmd" "project:core:cli")
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"cli" "core"}})))

(deftest arguments--single-project+all
  (is (= (test-arglist "cmd" "project:core" ":all")
         {:is-all true
          :is-run-all-brick-tests true
          :is-run-project-tests true
          :selected-projects #{"core"}})))

(deftest arguments--single-project+all-bricks
  (is (= (test-arglist "cmd" "project:core" ":all-bricks")
         {:is-all false
          :is-run-all-brick-tests true
          :is-run-project-tests false
          :selected-projects #{"core"}})))

(deftest arguments--test-project
  (is (= (test-arglist "cmd" ":project")
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests true
          :selected-projects #{}})))

(deftest arguments--selected-profiles
  (is (= (active-profiles-params "cmd" "+admin" "project:a" "+user")
         {:selected-profiles #{"admin" "user"}})))
