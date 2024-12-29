(ns com.example.comp-clj-cljc-cljs.core-test
  (:require [cljs.test :refer [deftest is]]
            [com.example.comp-clj-cljc-cljs.core :as core]))

(deftest test-fn-8
  (is (= 32 (core/fn-8 [2 3 4]))))
