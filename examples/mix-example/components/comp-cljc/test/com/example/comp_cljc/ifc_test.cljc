(ns com.example.comp-cljc.ifc-test
  (:require #?(:clj [clojure.test :refer [deftest is]]
               :cljs [cljs.test :refer [deftest is]])
            [com.example.comp-cljc.ifc :as comp-cljc]))

(deftest test-fn-4
  (is (= 3 (comp-cljc/fn-4 1 2 3))))

(deftest test-fn-5
  (is (= 6144 (comp-cljc/fn-5 {:key-1 4 :key-2 5 :key-3 6}))))
