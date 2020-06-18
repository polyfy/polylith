(ns polylith.validate.missing-data-defs-test
  (:require [clojure.test :refer :all]
            [polylith.validate.missing-data-defs :as missing-defs]))

(def interfaces '[{:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc", :type "data"}
                                 {:name "func1", :type "function", :parameters ["a"]}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "user"
                   :type "interface",
                   :definitions [{:name "data1", :type "data"}
                                 {:name "data2", :type "data" :sub-ns "a.b"}
                                 {:name "func1", :type "function", :parameters ["a"]}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "invoice"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data"}
                                             {:name "func1", :type "function", :parameters ["a"]}]}}
                  {:name "invoice2"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data"}
                                             {:name "func1", :type "function", :parameters ["a"]}]}}
                  {:name "user1"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "data1" :type "data"}
                                             {:name "data2", :type "data" :sub-ns "a.b"}
                                             {:name "func1", :type "function", :parameters []}]}}
                  {:name "user2"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"]}]}}])

(deftest errors--when-having-missing-data-in-a-component--return-error
  (is (= ["Missing definitions in the interface of user2: data1, a.b/data2"]
         (missing-defs/errors interfaces components))))
