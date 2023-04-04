(ns com.for.test.cli.core-test
  (:require [clojure.test :refer :all]
            [com.for.test.util.interface :as util]
            [com.for.test.helper.core-test :as helper-test]))

(deftest successful-test
  (is (= util/abc 123)))

(deftest data-test
  (is (= helper-test/data "data")))
