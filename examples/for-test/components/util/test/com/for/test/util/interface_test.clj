(ns com.for.test.util.interface-test
  (:require [clojure.test :as test :refer :all]
            [com.for.test.util.ifc :as util]))

(deftest successful-test
  (is (= util/abc 123)))
