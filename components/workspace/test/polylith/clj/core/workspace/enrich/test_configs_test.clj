(ns polylith.clj.core.workspace.enrich.test-configs-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.enrich.test-configs :as test-configs]))

(deftest merge-vector-values-if-both-are-vectors
  (is (= {:a [1 2]}
         (test-configs/merge-data {:a [1]} [{:a [2]} []]))))

(deftest merge-vector-values-if-both-are-vectors-multi-levels
  (is (= {:a {:b [1 2]}}
         (test-configs/merge-data {:a {:b [1]}} [{:a {:b [2]}} []]))))

(deftest replace-with-value-if-not-both-are-vectors
  (is (= {:a [2]}
         (test-configs/merge-data {:a 1} [{:a [2]} []]))))

(deftest merge-multiple-values
  (is (= {:a 2
          :b {:c 4
              :e 5
              :f 6}
          :d [:i :j]
          :x 123}
         (test-configs/merge-data {:a 1
                                   :b {:c 3
                                       :f 6}
                                   :d [:i]}
                                  [{:a 2
                                    :b {:c 4
                                        :e 5}
                                    :d [:j]
                                    :x 123}
                                   []]))))
