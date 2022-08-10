(ns com.for.test.a-okay.interface-test
  (:require [clojure.test :as test :refer :all]
            [com.for.test.a-okay.interface :as a-okay]))

(deftest successful-test
  (is (= 1 1)))
