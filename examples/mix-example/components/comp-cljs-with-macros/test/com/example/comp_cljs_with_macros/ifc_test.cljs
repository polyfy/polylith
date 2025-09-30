(ns com.example.comp-cljs-with-macros.ifc-test
  (:require [cljs.test :refer [deftest is]]
            [com.example.comp-cljs-with-macros.ifc :as comp-cljs-with-macros]))

(deftest test-fn-9
  (is (= 24 (comp-cljs-with-macros/fn-9 [2 3 4]))))
