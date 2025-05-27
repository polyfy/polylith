(ns com.example.comp-clj-cljc.util-test
  (:require #?(:clj [clojure.test :refer [deftest is]]
               :cljs [cljs.test :refer [deftest is]])
            [com.example.comp-clj-cljc.util :as util]))

(deftest test-fn-2
  (is [3] (util/fn-2 3)))
