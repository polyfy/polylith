(ns com.for.test.company.interface-test
  (:require #?@(:clj  [[clojure.test :refer :all]]
                :cljs [[cljs.test :refer-macros [deftest is testing]]])))

;; Test reader conditions, see https://github.com/polyfy/polylith/issues/208

(deftest dummy-test
  (is (= 1 1)))
