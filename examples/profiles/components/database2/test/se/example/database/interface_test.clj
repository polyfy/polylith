(ns se.example.database.interface-test
  (:require [clojure.test :as test :refer :all]))

(deftest always-wrong
  (is (= 1 2)))
