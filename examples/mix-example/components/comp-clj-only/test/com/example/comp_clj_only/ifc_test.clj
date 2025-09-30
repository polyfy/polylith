(ns com.example.comp-clj-only.ifc-test
  (:require [clojure.test :refer [deftest is]]
            [com.example.comp-clj-only.ifc :as comp-clj-only]))

(deftest test-fn-6
  (is (= 9 (comp-clj-only/fn-6 1 2 3))))
