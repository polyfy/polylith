(ns polylith.clj.core.change.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.core :as core]))

(deftest project-with-a-component-that-has-test-only-changes--is-affected
  (is (= true
         (core/project-affected? {:name "service"
                                  :component-names {:test ["test-utils"]}
                                  :base-names {}}
                                 ["test-utils"] [] []))))
