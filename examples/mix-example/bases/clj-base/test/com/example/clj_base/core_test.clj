(ns com.example.clj-base.core-test
  (:require [clojure.test :refer [deftest is]]
            [com.example.clj-base.core :as core]))

(deftest dummy-test
  (is (= 1 1)))
