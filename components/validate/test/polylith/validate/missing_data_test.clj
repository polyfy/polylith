(ns polylith.validate.missing-data-test
  (:require [clojure.test :refer :all]
            [polylith.validate.missing-data :as missing-data]))

(def interfaces '[{:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc", :type "data" :ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :ns ""}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "user"
                   :type "interface",
                   :definitions [{:name "data1", :type "data" :ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :ns ""}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "invoice"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :ns ""}]}}
                  {:name "invoice2"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :ns ""}]}}
                  {:name "user1"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "data1" :type "data" :ns ""}
                                             {:name "func1", :type "function", :parameters [] :ns ""}]}}
                  {:name "user2"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :ns ""}]}}])

(deftest errors--missing-data-in-a-component--returns-error
  (is (= ["Missing definition in the user2 component: data1"]
         (missing-data/errors interfaces components))))
