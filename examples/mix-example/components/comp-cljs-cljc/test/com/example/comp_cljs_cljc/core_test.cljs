(ns com.example.comp-cljs-cljc.core-test
  (:require [cljs.test :refer [deftest is]]
            [com.example.comp-cljs-cljc.core :as core]))

(deftest test-fn-1
  (is (= [1 2] (core/fn-1 1 2))))
