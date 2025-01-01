(ns com.example.comp-cljs-only.ifc-test
  (:require [cljs.test :refer [deftest is]]
            [com.example.comp-cljs-only.ifc :as comp-cljs-only]))

(deftest test-fn-7
  (is (= 9 (comp-cljs-only/fn-7 1 2 3))))

(deftest test-fn-10
  (is (= 60 (comp-cljs-only/fn-10))))
