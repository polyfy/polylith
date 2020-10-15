(ns polylith.clj.core.user-input.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn test-params [& args]
  (select-keys (user-input/extract-params args)
               [:is-all
                :is-run-all-brick-tests
                :is-run-project-tests
                :selected-projects]))

(defn active-profiles-params [& args]
  (select-keys (user-input/extract-params args)
               [:selected-profiles]))

(deftest parameters--no-arguments
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{}}
         (test-params))))

(deftest parameters--a-single-project
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"core"}}
         (test-params "cmd" "project:core"))))

(deftest parameters--a-list-of-projects
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"cli" "core"}}
         (test-params "cmd" "project:core:cli"))))

(deftest parameters--single-project+all
  (is (= {:is-all true
          :is-run-all-brick-tests true
          :is-run-project-tests true
          :selected-projects #{"core"}}
         (test-params "cmd" "project:core" ":all"))))

(deftest parameters--single-project+all-bricks
  (is (= {:is-all false
          :is-run-all-brick-tests true
          :is-run-project-tests false
          :selected-projects #{"core"}}
         (test-params "cmd" "project:core" ":all-bricks"))))

(deftest parameters--test-project
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests true
          :selected-projects #{}}
         (test-params "cmd" ":project"))))

(deftest parameters--selected-profiles
  (is (= {:selected-profiles #{"admin" "user"}}
         (active-profiles-params "cmd" "+admin" "project:a" "+user"))))
