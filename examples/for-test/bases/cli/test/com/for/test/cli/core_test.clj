(ns com.for.test.cli.core-test
  (:require [clojure.test :refer :all]
            [com.for.test.util.interface :as util]))

(deftest successful-test
  (is (= util/abc 123)))
