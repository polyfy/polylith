(ns integrant.embedded-pg.interface-test
  (:require [clojure.test :as test :refer :all]
            [integrant.embedded-pg.interface :as postgres]))

(deftest dummy-test
  (is (= 1 1)))
