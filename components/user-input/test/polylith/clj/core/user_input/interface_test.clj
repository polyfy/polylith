(ns polylith.clj.core.user-input.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn test-params [& args]
  (select-keys (user-input/extract-params args)
               [:is-all
                :is-run-all-brick-tests
                :is-run-env-tests
                :selected-environments]))

(defn active-profiles-params [& args]
  (select-keys (user-input/extract-params args)
               [:selected-profiles]))

(deftest parameters--no-arguments
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-env-tests false
          :selected-environments #{}}
         (test-params))))

(deftest parameters--a-single-env
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-env-tests false
          :selected-environments #{"core"}}
         (test-params "cmd" "env:core"))))

(deftest parameters--a-list-of-envs
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-env-tests false
          :selected-environments #{"cli" "core"}}
         (test-params "cmd" "env:core:cli"))))

(deftest parameters--single-env+all
  (is (= {:is-all true
          :is-run-all-brick-tests true
          :is-run-env-tests true
          :selected-environments #{"core"}}
         (test-params "cmd" "env:core" ":all"))))

(deftest parameters--single-env+all-bricks
  (is (= {:is-all false
          :is-run-all-brick-tests true
          :is-run-env-tests false
          :selected-environments #{"core"}}
         (test-params "cmd" "env:core" ":all-bricks"))))

(deftest parameters--test-env
  (is (= {:is-all false
          :is-run-all-brick-tests false
          :is-run-env-tests true
          :selected-environments #{}}
         (test-params "cmd" ":env"))))

(deftest parameters--selected-profiles
  (is (= {:selected-profiles #{"admin" "user"}}
         (active-profiles-params "cmd" "+admin" "env:a" "+user"))))
