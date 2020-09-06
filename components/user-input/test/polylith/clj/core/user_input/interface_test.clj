(ns polylith.clj.core.user-input.interface-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn test-params [& args]
  (select-keys (user-input/extract-params args)
               [:run-all-brick-tests?
                :run-env-tests?
                :selected-environments]))

(defn active-dev-profiles-params [& args]
  (select-keys (user-input/extract-params args)
               [:active-dev-profiles]))

(deftest parameters--no-arguments
  (is (= {:run-all-brick-tests? false
          :run-env-tests? false
          :selected-environments #{}}
         (test-params))))

(deftest parameters--a-single-env
  (is (= {:run-all-brick-tests? false
          :run-env-tests? false
          :selected-environments #{"core"}}
         (test-params "cmd" "env:core"))))

(deftest parameters--a-list-of-envs
  (is (= {:run-all-brick-tests? false
          :run-env-tests? false
          :selected-environments #{"cli" "core"}}
         (test-params "cmd" "env:core:cli"))))

(deftest parameters--single-env+all
  (is (= {:run-all-brick-tests? true
          :run-env-tests? true
          :selected-environments #{"core"}}
         (test-params "cmd" "env:core" ":all"))))

(deftest parameters--single-env+all-bricks
  (is (= {:run-all-brick-tests? true
          :run-env-tests? false
          :selected-environments #{"core"}}
         (test-params "cmd" "env:core" ":all-bricks"))))

(deftest parameters--test-env
  (is (= {:run-all-brick-tests? false
          :run-env-tests? true
          :selected-environments #{}}
         (test-params "cmd" ":env"))))

(deftest parameters--active-dev-profiles
  (is (= {:active-dev-profiles #{"admin" "user"}}
         (active-dev-profiles-params "cmd" "+admin" "env:a" "+user"))))
