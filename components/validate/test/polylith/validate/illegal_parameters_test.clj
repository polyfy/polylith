(ns polylith.validate.illegal-parameters-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-parameters :as illegal-parameters]))

(def interfaces '[{:name "auth"
                   :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
                   :implementing-components ["auth"]
                   :implementing-deps []}
                  {:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc" :type "data" :ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["b"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]
                   :implementing-components ["invoice" "invoice2"]
                   :implementing-deps ["user"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                 {:name "pay", :type "function", :parameters ["b"] :ns ""}]
                   :implementing-components ["payment"]
                   :implementing-deps ["invoice"]}
                  {:name "user"
                   :type "interface"
                   :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                 {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                 {:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}
                                 {:name "func4", :type "function", :parameters [] :ns ""}
                                 {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]
                   :implementing-components ["user1" "user2"]
                   :implementing-deps ["payment" "auth"]}])

(def components '[{:name "auth"
                   :type "component"
                   :imports [{:ns-path "auth/interface.clj", :imports ["auth.core"]}
                             {:ns-path "auth/core.clj", :imports []}]
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"] :ns ""}]}
                   :dependencies []}
                  {:name "invoice"
                   :type "component"
                   :imports [{:ns-path "invoice/interface.clj", :imports []}
                             {:ns-path "invoice/core.clj", :imports ["user.interface"]}]
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :ns ""}
                                             {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :type "component"
                   :imports [{:ns-path "invoice/interface.clj", :imports []}
                             {:ns-path "invoice/core.clj", :imports []}]
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"] :ns ""}
                                             {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]}
                   :dependencies []}
                  {:name "payment"
                   :type "component"
                   :imports [{:ns-path "payment/interface.clj", :imports ["payment.core"]}
                             {:ns-path "payment/core.clj", :imports ["invoice.interface"]}]
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                             {:name "pay", :type "function", :parameters ["b"] :ns ""}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :type "component"
                   :imports [{:ns-path "user/interface.clj", :imports []}
                             {:ns-path "user/core.clj", :imports ["payment.interface"]}]
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                             {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                             {:name "func4", :type "function2", :parameters [] :ns ""}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :type "component"
                   :imports [{:ns-path "user/interface.clj", :imports []}
                             {:ns-path "user/core.clj", :imports ["auth.interface"]}]
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]}
                   :dependencies ["auth"]}])

(def interfaces2 '[{:name "auth",
                    :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
                    :implementing-components ["auth"]
                    :implementing-deps []}
                   {:name "invoice",
                    :type "interface"
                    :definitions [{:name "abc" :type "data" :ns ""}
                                  {:name "macro1", :type "macro", :parameters ["a"] :ns ""}
                                  {:name "macro1", :type "macro", :parameters ["b"] :ns ""}
                                  {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}
                                  {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]
                    :implementing-components ["invoice" "invoice2"]
                    :implementing-deps ["user"]}
                   {:name "payment"
                    :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                  {:name "pay", :type "function", :parameters ["b"] :ns ""}]
                    :implementing-components ["payment"]
                    :implementing-deps ["invoice"]}
                   {:name "user"
                    :type "interface"
                    :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                  {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                  {:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                  {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                  {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}
                                  {:name "func4", :type "function", :parameters [] :ns ""}
                                  {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]
                    :implementing-components ["user1" "user2"]
                    :implementing-deps ["payment" "auth"]}])

(def components2 '[{:name "auth"
                    :type "component"
                    :imports [{:ns-path "auth/interface.clj", :imports ["auth.core"]}
                              {:ns-path "auth/core.clj", :imports []}]
                    :interface {:name "auth"
                                :definitions [{:name "add-two", :type "function", :parameters ["x"] :ns ""}]}
                    :dependencies []}
                   {:name "invoice"
                    :type "component"
                    :imports [{:ns-path "invoice/interface.clj", :imports []}
                              {:ns-path "invoice/core.clj", :imports ["user.interface"]}]
                    :interface {:name "invoice"
                                :definitions [{:name "abc" :type "data" :ns ""}
                                              {:name "macro1", :type "macro", :parameters ["a"] :ns "sub"}
                                              {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}]}
                    :dependencies ["user"]}
                   {:name "invoice2"
                    :type "component"
                    :imports [{:ns-path "invoice/interface.clj", :imports []}
                              {:ns-path "invoice/core.clj", :imports []}]
                    :interface {:name "invoice"
                                :definitions [{:name "macro1", :type "macro", :parameters ["b"] :ns "sub"}
                                              {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]}
                    :dependencies []}
                   {:name "payment",
                    :type "component"
                    :imports [{:ns-path "payment/interface.clj", :imports ["payment.core"]}
                              {:ns-path "payment/core.clj", :imports ["invoice.interface"]}]
                    :interface {:name "payment"
                                :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                              {:name "pay", :type "function", :parameters ["b"] :ns ""}]}
                    :dependencies ["invoice"]}
                   {:name "user1"
                    :type "component"
                    :imports [{:ns-path "user/interface.clj", :imports []}
                              {:ns-path "user/core.clj", :imports ["payment.interface"]}]
                    :interface {:name "user"
                                :definitions [{:name "macro1", :type "macro", :parameters [] :ns ""}
                                              {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                              {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                              {:name "func4", :type "function", :parameters [] :ns ""}
                                              {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]}
                    :dependencies ["payment"]}
                   {:name "user2"
                    :type "component"
                    :imports [{:ns-path "user/interface.clj", :imports []}
                              {:ns-path "user/core.clj", :imports ["auth.interface"]}]
                    :interface {:name "user"
                                :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                              {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}
                                              {:name "func5", :type "function", :parameters ["a" "b" "c" "d"] :ns ""}]}
                    :dependencies ["auth"]}])

(deftest warnings--when-have-functions-with-same-arity-but-with-different-parameter-lists--return-warnings
  (is (= ["Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
          "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"]
         (illegal-parameters/warnings interfaces components))))

(deftest warnings--when-have-macros-with-same-arity-but-with-different-parameter-lists--return-warnings
  (is (= ["Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
          "Macro in the invoice component is also defined in invoice2 but with a different parameter list: sub.macro1[a], sub.macro1[b]"]
         (illegal-parameters/warnings interfaces2 components2))))

(deftest errors--when-have-duplicated-parameter--return-error
  (is (= ["Duplicated parameter lists found in the payment component: pay[a], pay[b]"]
         (illegal-parameters/errors components))))
