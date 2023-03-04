(ns missing.user.interface-test
  (:require [clojure.test :as test :refer :all]
            [missing.test-helper.interface :as test-helper]))

(deftest dummy-test
  (is (= test-helper/test-data 123)))
