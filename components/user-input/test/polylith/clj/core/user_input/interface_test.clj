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
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{}}
         (test-arglist))))

(deftest arguments--a-single-project
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"core"}}
         (test-arglist "cmd" "project:core"))))

(deftest arguments--a-list-of-projects
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests false
          :selected-projects #{"cli" "core"}}
         (test-arglist "cmd" "project:core:cli"))))

(deftest arguments--single-project+all
  (is (= {:is-all true
          :is-run-all-brick-tests true
          :is-run-project-tests true
          :selected-projects #{"core"}}
         (test-arglist "cmd" "project:core" ":all"))))

(deftest arguments--single-project+all-bricks
  (is (= {:is-all false
          :is-run-all-brick-tests true
          :is-run-project-tests false
          :selected-projects #{"core"}}
         (test-arglist "cmd" "project:core" ":all-bricks"))))

(deftest arguments--test-project
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-project-tests true
          :selected-projects #{}}
         (test-arglist "cmd" ":project"))))

(deftest arguments--selected-profiles
  (is (= {:selected-profiles #{"admin" "user"}}
         (active-profiles-params "cmd" "+admin" "project:a" "+user"))))
