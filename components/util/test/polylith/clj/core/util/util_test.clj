(ns polylith.clj.core.util.util-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface :as util]))

(deftest find-first--when-having-a-list-with-one-odd-value--return-the-odd-value
  (is (= (util/find-first odd? [2 4 5 6])
         5)))

(deftest ordered-map--a-list-with-keys-and-values-with-one-nil-value--should-return-a-map-with-nil-values-excluded
  (is (= (util/ordered-map :a 1
                           :b 2
                           :c nil
                           :d 4)
         {:a 1
          :b 2
          :d 4})))
