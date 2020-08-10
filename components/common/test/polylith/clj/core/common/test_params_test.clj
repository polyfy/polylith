(ns polylith.clj.core.common.test-params-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.test-settings :as test-params]))

(deftest params--no-arguments
  (is (= {:run-all? false
          :run-env-tests? false
          :selected-environments #{}}
         (test-params/settings []))))

(deftest params--a-single-env
  (is (= {:run-all? false
          :run-env-tests? false
          :selected-environments #{"core"}}
         (test-params/settings ["env:core"]))))

(deftest params--a-list-of-envs
  (is (= {:run-all? false
          :run-env-tests? false
          :selected-environments #{"cli" "core"}}
         (test-params/settings ["env:core:cli"]))))

(deftest params--single-env+all
  (is (= {:run-all? true
          :run-env-tests? true
          :selected-environments #{"core"}}
         (test-params/settings ["env:core" ":all"]))))

(deftest params--single-env+all-bricks
  (is (= {:run-all? true
          :run-env-tests? false
          :selected-environments #{"core"}}
         (test-params/settings ["env:core" ":all-bricks"]))))

(deftest params--single-env+all-bricks2
  (is (= {:run-all? false
          :run-env-tests? true
          :selected-environments #{}}
         (test-params/settings [":test-env"]))))
