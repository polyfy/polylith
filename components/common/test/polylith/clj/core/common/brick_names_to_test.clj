(ns polylith.clj.core.common.brick-names-to-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.core :as core]))

(deftest all-bricks
  (is (= #{"a" "b" "c"}
         (core/brick-names-to-test
           nil
           ["a" "b" "c"]))))

(deftest include-two-bricks
  (is (= #{"a" "b"}
         (core/brick-names-to-test
           {:include ["a" "b"]}
           ["a" "b" "c"]))))

(deftest exclude-one-brick
  (is (= #{"a" "c"}
         (core/brick-names-to-test
           {:exclude ["b"]}
           ["a" "b" "c"]))))

(deftest include-two-and-exclude-one-brick
  (is (= #{"a"}
         (core/brick-names-to-test
           {:include ["a" "b"]
            :exclude ["b"]}
           ["a" "b" "c"]))))
