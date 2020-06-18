(ns polylith.validate.missing-function-and-macro-defs-test
  (:require [clojure.test :refer :all]
            [polylith.validate.missing-function-and-macro-defs :as missing-defs]))

(def interfaces '[{:name "auth"
                   :definitions [{:name "add-two", :type "function", :parameters ["x"] :sub-ns ""}]
                   :implementing-components ["auth"]}
                  {:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc" :type "data" :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["b"] :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["a" "b"] :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["x" "y"] :sub-ns ""}]
                   :implementing-components ["invoice" "invoice2"] :sub-ns ""}
                  {:name "payFment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"] :sub-ns ""}
                                 {:name "pay", :type "function", :parameters ["b"] :sub-ns ""}]
                   :implementing-components ["payment"]}
                  {:name "user"
                   :type "interface"
                   :definitions [{:name "func1", :type "function", :parameters [] :sub-ns ""}
                                 {:name "func2", :type "function", :parameters ["a" "b"] :sub-ns ""}
                                 {:name "func2", :type "function", :parameters ["x" "y"] :sub-ns ""}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"] :sub-ns ""}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"] :sub-ns ""}
                                 {:name "func4", :type "function", :parameters [] :sub-ns "subns"}
                                 {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :sub-ns ""}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :type "component"
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"] :sub-ns ""}]}}
                  {:name "invoice"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["a" "b"] :sub-ns ""}]}}
                  {:name "invoice2"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"] :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["x" "y"] :sub-ns ""}]}}
                  {:name "payment"
                   :type "component"
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"] :sub-ns ""}
                                             {:name "pay", :type "function", :parameters ["b"] :sub-ns ""}]}}
                  {:name "user1"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters [] :sub-ns ""}
                                             {:name "func2", :type "function", :parameters ["a" "b"] :sub-ns ""}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"] :sub-ns ""}
                                             {:name "func4", :type "function", :parameters [] :sub-ns "subns"}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :sub-ns ""}]}}
                  {:name "user2"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :sub-ns ""}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"] :sub-ns ""}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :sub-ns ""}]}}])

(deftest errors--when-having-a--component-with-missing-functions--return-error-message
  (is (= ["Missing function definitions in the interface of user2: func1[], subns/func4[]"]
         (missing-defs/errors interfaces components))))

(deftest errors--when-having-a-component-with-missing-functions-and-macros--return-error-message
  (let [interfaces-with-func1-in-comp1-as-macro (assoc-in interfaces [3 :definitions 0 :type] "macro")
        components-with-func1-in-comp1-as-macro (assoc-in components [4 :interface :definitions 0 :type] "macro")]
    (is (= ["Missing function and macro definitions in the interface of user2: func1[], subns/func4[]"]
           (missing-defs/errors interfaces-with-func1-in-comp1-as-macro
                                components-with-func1-in-comp1-as-macro)))))
