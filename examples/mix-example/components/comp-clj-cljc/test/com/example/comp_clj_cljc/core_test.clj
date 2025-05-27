(ns com.example.comp-clj-cljc.core-test
  (:require [clojure.test :refer [deftest is]]
            [com.example.comp-clj-cljc.core :as core]))

(deftest test-fn-1
  (is [1 2] (core/fn-1 1 2)))
