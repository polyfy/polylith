(ns com.localdep.invoicer.interface-test
  (:require [clojure.test :refer :all]
            [com.localdep.test-helper.interface :as test-helper]
            [com.localdep.migrate-me.core :as mm-core]))

(deftest dummy-test
  (is (= 123 mm-core/one-two-three)))
