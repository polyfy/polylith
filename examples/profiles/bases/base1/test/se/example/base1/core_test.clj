(ns se.example.base1.core-test
  (:require [clojure.test :as test :refer :all]
            [se.example.base1.core :as core]
            [se.example.base2.core-test :as base2-test]))

(deftest abc-test
  (is (= 123 base2-test/abc)))
