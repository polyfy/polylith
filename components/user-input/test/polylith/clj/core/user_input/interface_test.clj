(ns polylith.clj.core.user-input.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn test-params [& args]
  (select-keys (user-input/extract-arguments args)
               [:is-all
                :is-run-all-brick-tests
                :is-run-project-tests
                :selected-projects]))

(defn active-profiles-params [& args]
  (select-keys (user-input/extract-arguments args)
               [:selected-profiles]))

(deftest parameters--no-arguments
  (is (= (test-params)
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{}})))

(deftest parameters--a-single-project
  (is (= (test-params "cmd" "project:core")
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"core"}})))

(deftest parameters--a-list-of-projects
  (is (= (test-params "cmd" "project:core:cli")
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"cli" "core"}})))

(deftest parameters--single-project+all
  (is (= (test-params "cmd" "project:core" ":all")
         {:is-all true
          :is-run-all-brick-tests true
          :is-run-project-tests true
          :selected-projects #{"core"}})))

(deftest parameters--single-project+all-bricks
  (is (= (test-params "cmd" "project:core" ":all-bricks")
         {:is-all false
          :is-run-all-brick-tests true
          :is-run-project-tests false
          :selected-projects #{"core"}})))

(deftest parameters--test-project
  (is (= (test-params "cmd" ":project")
         {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests true
          :selected-projects #{}})))

(deftest parameters--selected-profiles
  (is (= (active-profiles-params "cmd" "+admin" "project:a" "+user")
         {:selected-profiles #{"admin" "user"}})))
