(ns polylith.clj.core.workspace.fromdisk.non-top-namespace-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.fromdisk.non-top-namespace :as sut]))

(deftest bricks-to-disable__disable_no_warnings
  (is (= nil
         (sut/bricks-to-disable [false []] {:warning 1}))))

(deftest bricks-to-disable__disable_all
  (is (= [true []]
         (sut/bricks-to-disable [false []] {:warning 205}))))

(deftest bricks-to-disable__disable_some
  (is (= [false ["a" "b"]]
         (sut/bricks-to-disable [false []] {:warning 205
                                            :bricks ["a" "b"]}))))
