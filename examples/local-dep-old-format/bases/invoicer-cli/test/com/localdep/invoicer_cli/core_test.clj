(ns com.localdep.invoicer-cli.core-test
  (:require [clojure.test :refer :all]
            [com.localdep.invoicer-cli.core :as core]))

(deftest dummy-test
  (is (= "abc123" (core/-main))))
