(ns com.example.comp-cljs-cljc.util-test
  (:require #?(:clj [clojure.test :refer [deftest is]]
               :cljs [cljs.test :refer [deftest is]])
            [com.example.comp-cljs-cljc.util :as util]))

(deftest test-fn-3
  (is [4] (util/fn-3 4)))
