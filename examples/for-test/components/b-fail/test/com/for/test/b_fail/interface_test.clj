(ns com.for.test.b-fail.interface-test
  (:require [clojure.test :as test :refer :all]
            [com.for.test.b-fail.interface :as b-fail]))

(deftest failing-test
  (is (= 1 2)))
