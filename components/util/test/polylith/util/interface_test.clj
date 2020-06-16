(ns polylith.util.interface-test
  (:require [clojure.test :refer :all]
            [polylith.util.interface :as util]))

(deftest ordered-map--a-list-with-keys-and-values-with-one-nil-value--should-return-a-map-with-nil-values-excluded
  (is (= {:a 1
          :b 2
          :d 4}
         (util/ordered-map :a 1
                           :b 2
                           :c nil
                           :d 4))))
