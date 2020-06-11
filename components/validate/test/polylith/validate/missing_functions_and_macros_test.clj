(ns polylith.validate.missing-functions-and-macros-test
  (:require [clojure.test :refer :all]
            [polylith.validate.missing-functions-and-macros :as missing]))

(def interfaces '[{:name "auth"
                   :definitions [{:name "add-two", :type "function", :parameters ["x"] :ns ""}]
                   :implementing-components ["auth"]}
                  {:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc" :type "data" :ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["b"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]
                   :implementing-components ["invoice" "invoice2"] :ns ""}
                  {:name "payFment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                 {:name "pay", :type "function", :parameters ["b"] :ns ""}]
                   :implementing-components ["payment"]}
                  {:name "user"
                   :type "interface"
                   :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                 {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                 {:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}
                                 {:name "func4", :type "function", :parameters [] :ns ""}
                                 {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :type "component"
                   :imports [{:ns-path "auth/interface.clj", :imports ["auth.core"]}
                             {:ns-path "auth/core.clj", :imports []}]
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"] :ns ""}]}}
                  {:name "invoice"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :ns ""}
                                             {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}]}}
                  {:name "invoice2"
                   :type "component"
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"] :ns ""}
                                             {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]}}
                  {:name "payment"
                   :type "component"
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                             {:name "pay", :type "function", :parameters ["b"] :ns ""}]}}
                  {:name "user1"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                             {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                             {:name "func4", :type "function", :parameters [] :ns ""}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]}}
                  {:name "user2"
                   :type "component"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]}}])

(deftest errors--if-component-with-missing-functions--then-return-error-message
  (is (= ["Missing functions in component user2: func1[], func4[]"]
         (missing/errors interfaces components))))

(deftest errors--if-component-with-missing-functions-and-macros--then-return-error-message
  (let [interfaces-with-func1-in-comp1-as-macro (assoc-in interfaces [3 :definitions 0 :type] "macro")
        components-with-func1-in-comp1-as-macro (assoc-in components [4 :interface :definitions 0 :type] "macro")]
    (is (= ["Missing functions and macros in component user2: func1[], func4[]"]
           (missing/errors interfaces-with-func1-in-comp1-as-macro
                           components-with-func1-in-comp1-as-macro)))))
