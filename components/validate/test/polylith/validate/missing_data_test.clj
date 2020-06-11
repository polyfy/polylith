(ns polylith.validate.missing-data-test
  (:require [clojure.test :refer :all]
            [polylith.validate.missing-data :as missing-data]))

(def interfaces '[{:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc", :type "data" :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "user"
                   :type "interface",
                   :definitions [{:name "data1", :type "data" :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "invoice"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}]}}
                  {:name "invoice2"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}]}}
                  {:name "user1"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "data1" :type "data" :sub-ns ""}
                                             {:name "func1", :type "function", :parameters [] :sub-ns ""}]}}
                  {:name "user2"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :sub-ns ""}]}}])

(deftest errors--missing-data-in-a-component--returns-error
  (is (= ["Missing definition in the user2 component: data1"]
         (missing-data/errors interfaces components))))
