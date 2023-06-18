(ns polylith.clj.core.deps.project-brick-deps-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.deps.project-brick-deps :as pbd]))

(deftest update-indirect-deps--prepare-for-circular-deps
  (let [brick-id->deps (atom {"a" {:direct ["b"]
                                   :indirect #{}
                                   :completed? false}
                              "b" {:direct ["a"]
                                   :indirect #{}
                                   :completed? false}})]
    (is (= (pbd/update-indirect-deps! "a" "b"
                                      {"a" ["b"]
                                       "b" ["a"]}
                                      brick-id->deps
                                      #{"a"})
           {"a" {:completed? true
                 :direct     ["b"]
                 :indirect   #{"a" "b"}
                 :paths      [["b" "a"]]}
            "b" {:completed? true
                 :direct     ["a"]
                 :indirect   #{"b"}
                 :paths      [["a"]]}}))))

(deftest finalise-deps--check-for-circular-deps
  (let [brick-id->deps {"a" {:direct ["b"], :indirect #{"a" "b"}, :completed? true, :paths [["b" "a"]]},
                        "b" {:direct ["a"], :indirect #{"b"}, :completed? true, :paths [["a"]]}}
        ifc->comp {"a" "a"
                   "b" "b"}]
    (is (= (pbd/finalise-deps "a" brick-id->deps ifc->comp #{"a" "b"} #{"a" "b"} #{})
           {:circular ["a" "b" "a"]
            :direct   ["b"]
            :indirect ["a"]}))))
