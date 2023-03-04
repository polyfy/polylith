(ns missing.util.interface-test
  (:require [clojure.test :as test :refer :all]
            [missing.util.interface :as util]))

(deftest dummy-test
  (is (= (util/hello)
         "hello")))
