(ns polylith.common.extract-aliases-test
  (:require [clojure.test :refer :all]
            [polylith.common.extract-aliases :as extract-aliases]))

(def config {:aliases {:env/abc     1
                       :env/abc-test  2
                       :env/prod      3
                       :env/prod-test 4
                       :env/core      5
                       :env/core-test 6
                       :dev           7}})

(deftest extract--when-extracting-an-environment--return-that-environment
  (is (= [[:env/core 5]]
         (extract-aliases/extract config "core"))))

(deftest extract--when-extracting-an-environment-with-tests--return-that-environment-and-the-tests
  (is (= [[:env/core 5]
          [:env/core-test 6]]
         (extract-aliases/extract config "core" true))))

(deftest extract--when-extracting-a-test-environment-with-tests--return-the-corresponding-environment-and-the-test
  (is (= [[:env/core 5]
          [:env/core-test 6]]
         (extract-aliases/extract config "core-test" true))))
