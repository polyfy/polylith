(ns com.example.comp-clj-cljc-cljs.ifc-test
  (:require #?(:clj [clojure.test :refer [deftest is]]
               :cljs [cljs.test :refer [deftest is]])
            [com.example.comp-clj-cljc-cljs.ifc :as comp-clj-cljc-cljs]))

(deftest test-fn-8
  (is (= 32 (comp-clj-cljc-cljs/fn-8 {:key-1 2 :key-2 3 :key-3 4}))))
