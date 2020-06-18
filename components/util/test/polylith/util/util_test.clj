(ns polylith.util.util-test
  (:require [clojure.test :refer :all]
            [polylith.util.interface :as util]))

(deftest find-first--when-having-a-list-with-one-odd-value--return-the-odd-value
  (is (= 5
         (util/find-first odd? [2 4 5 6]))))

(deftest ordered-map--a-list-with-keys-and-values-with-one-nil-value--should-return-a-map-with-nil-values-excluded
  (is (= {:a 1
          :b 2
          :d 4}
         (util/ordered-map :a 1
                           :b 2
                           :c nil
                           :d 4))))
