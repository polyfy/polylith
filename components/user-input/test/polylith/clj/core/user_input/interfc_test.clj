(ns polylith.clj.core.user-input.interfc-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.user-input.interfc :as user-input]))

(defn test-params [& args]
  (select-keys (user-input/extract-params args)
               [:run-all?
                :run-env-tests?
                :selected-environments]))

(defn active-dev-profiles-params [& args]
  (select-keys (user-input/extract-params (conj args "cmd"))
               [:active-dev-profiles]))

(deftest parameters--no-arguments
  (is (= {:run-all? false
          :run-env-tests? false
          :selected-environments #{}}
         (test-params))))

(deftest parameters--a-single-env
  (is (= {:run-all? false
          :run-env-tests? false
          :selected-environments #{"core"}}
         (test-params "env:core"))))

(deftest parameters--a-list-of-envs
  (is (= {:run-all? false
          :run-env-tests? false
          :selected-environments #{"cli" "core"}}
         (test-params "env:core:cli"))))

(deftest parameters--single-env+all
  (is (= {:run-all? true
          :run-env-tests? true
          :selected-environments #{"core"}}
         (test-params "env:core" ":all"))))

(deftest parameters--single-env+all-bricks
  (is (= {:run-all? true
          :run-env-tests? false
          :selected-environments #{"core"}}
         (test-params "env:core" ":all-bricks"))))

(deftest parameters--test-env
  (is (= {:run-all? false
          :run-env-tests? true
          :selected-environments #{}}
         (test-params ":test-env"))))

(deftest parameters--active-dev-profiles
  (is (= {:active-dev-profiles #{"admin" "user"}}
         (active-dev-profiles-params "+admin" "env:a" "+user"))))
