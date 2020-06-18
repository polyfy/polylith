(ns polylith.validate.illegal-parameters-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-parameters :as illegal-parameters]))

(def interfaces '[{:name "auth"
                   :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
                   :implementing-components ["auth"]
                   :implementing-interface-deps []}
                  {:name "invoice"
                   :type "interface"
                   :definitions [{:name "abc" :type "data"}
                                 {:name "func1", :type "function", :parameters ["a"]}
                                 {:name "func1", :type "function", :parameters ["b"]}
                                 {:name "func1", :type "function", :parameters ["a" "b"]}
                                 {:name "func1", :type "function", :parameters ["x" "y"]}]
                   :implementing-components ["invoice" "invoice2"]
                   :implementing-interface-deps ["user"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"]}
                                 {:name "pay", :type "function", :parameters ["b"]}]
                   :implementing-components ["payment"]
                   :implementing-interface-deps ["invoice"]}
                  {:name "user"
                   :type "interface"
                   :definitions [{:name "func1", :type "function", :parameters []}
                                 {:name "func2", :type "function", :parameters ["a" "b"]}
                                 {:name "func2", :type "function", :parameters ["x" "y"]}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"]}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"]}
                                 {:name "func4", :type "function", :parameters []}
                                 {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]
                   :implementing-components ["user1" "user2"]
                   :implementing-interface-deps ["payment" "auth"]}])

(def components '[{:name "auth"
                   :type "component"
                   :namespaces [{:name "auth/interface.clj", :imports ["auth.core"]}
                                {:name "auth/core.clj", :imports []}]
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"]}]}
                   :interface-deps []}
                  {:name "invoice"
                   :type "component"
                   :namespaces [{:name "invoice/interface.clj", :imports []}
                                {:name "invoice/core.clj", :imports ["user.interface"]}]
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data"}
                                             {:name "func1", :type "function", :parameters ["a"]}
                                             {:name "func1", :type "function", :parameters ["a" "b"]}]}
                   :interface-deps ["user"]}
                  {:name "invoice2"
                   :type "component"
                   :namespaces [{:name "invoice/interface.clj", :imports []}
                                {:name "invoice/core.clj", :imports []}]
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"]}
                                             {:name "func1", :type "function", :parameters ["x" "y"]}]}
                   :interface-deps []}
                  {:name "payment"
                   :type "component"
                   :namespaces [{:name "payment/interface.clj", :imports ["payment.core"]}
                                {:name "payment/core.clj", :imports ["invoice.interface"]}]
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"]}
                                             {:name "pay", :type "function", :parameters ["b"]}]}
                   :interface-deps ["invoice"]}
                  {:name "user1"
                   :type "component"
                   :namespaces [{:name "user/interface.clj", :imports []}
                                {:name "user/core.clj", :imports ["payment.interface"]}]
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters []}
                                             {:name "func2", :type "function", :parameters ["a" "b"]}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"]}
                                             {:name "func4", :type "function2", :parameters []}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]}
                   :interface-deps ["payment"]}
                  {:name "user2"
                   :type "component"
                   :namespaces [{:name "user/interface.clj", :imports []}
                                {:name "user/core.clj", :imports ["auth.interface"]}]
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"]}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"]}
                                             {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]}
                   :interface-deps ["auth"]}])

(def interfaces2 '[{:name "auth",
                    :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
                    :implementing-components ["auth"]
                    :implementing-interface-deps []}
                   {:name "invoice",
                    :type "interface"
                    :definitions [{:name "abc" :type "data"}
                                  {:name "macro1", :type "macro", :parameters ["a"]}
                                  {:name "macro1", :type "macro", :parameters ["b"]}
                                  {:name "func1", :type "function", :parameters ["a" "b"]}
                                  {:name "func1", :type "function", :parameters ["x" "y"]}]
                    :implementing-components ["invoice" "invoice2"]
                    :implementing-interface-deps ["user"]}
                   {:name "payment"
                    :definitions [{:name "pay", :type "function", :parameters ["a"]}
                                  {:name "pay", :type "function", :parameters ["b"]}]
                    :implementing-components ["payment"]
                    :implementing-interface-deps ["invoice"]}
                   {:name "user"
                    :type "interface"
                    :definitions [{:name "func1", :type "function", :parameters []}
                                  {:name "func2", :type "function", :parameters ["a" "b"]}
                                  {:name "func2", :type "function", :parameters ["x" "y"]}
                                  {:name "func3", :type "function", :parameters ["a" "b" "c"]}
                                  {:name "func3", :type "function", :parameters ["x" "y" "z"]}
                                  {:name "func4", :type "function", :parameters []}
                                  {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]
                    :implementing-components ["user1" "user2"]
                    :implementing-interface-deps ["payment" "auth"]}])

(def components2 '[{:name "auth"
                    :type "component"
                    :namespaces [{:name "auth/interface.clj", :imports ["auth.core"]}
                                 {:name "auth/core.clj", :imports []}]
                    :interface {:name "auth"
                                :definitions [{:name "add-two", :type "function", :parameters ["x"]}]}
                    :interface-deps []}
                   {:name "invoice"
                    :type "component"
                    :namespaces [{:name "invoice/interface.clj", :imports []}
                                 {:name "invoice/core.clj", :imports ["user.interface"]}]
                    :interface {:name "invoice"
                                :definitions [{:name "abc" :type "data"}
                                              {:name "macro1", :type "macro", :parameters ["a"] :sub-ns "sub"}
                                              {:name "func1", :type "function", :parameters ["a" "b"]}]}
                    :interface-deps ["user"]}
                   {:name "invoice2"
                    :type "component"
                    :namespaces [{:name "invoice/interface.clj", :imports []}
                                 {:name "invoice/core.clj", :imports []}]
                    :interface {:name "invoice"
                                :definitions [{:name "macro1", :type "macro", :parameters ["b"] :sub-ns "sub"}
                                              {:name "func1", :type "function", :parameters ["x" "y"]}]}
                    :interface-deps []}
                   {:name "payment",
                    :type "component"
                    :namespaces [{:name "payment/interface.clj", :imports ["payment.core"]}
                                 {:name "payment/core.clj", :imports ["invoice.interface"]}]
                    :interface {:name "payment"
                                :definitions [{:name "pay", :type "function", :parameters ["a"]}
                                              {:name "pay", :type "function", :parameters ["b"]}]}
                    :interface-deps ["invoice"]}
                   {:name "user1"
                    :type "component"
                    :namespaces [{:name "user/interface.clj", :imports []}
                                 {:name "user/core.clj", :imports ["payment.interface"]}]
                    :interface {:name "user"
                                :definitions [{:name "macro1", :type "macro", :parameters []}
                                              {:name "func2", :type "function", :parameters ["a" "b"]}
                                              {:name "func3", :type "function", :parameters ["a" "b" "c"]}
                                              {:name "func4", :type "function", :parameters []}
                                              {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]}
                    :interface-deps ["payment"]}
                   {:name "user2"
                    :type "component"
                    :namespaces [{:name "user/interface.clj", :imports []}
                                 {:name "user/core.clj", :imports ["auth.interface"]}]
                    :interface {:name "user"
                                :definitions [{:name "func2", :type "function", :parameters ["x" "y"]}
                                              {:name "func3", :type "function", :parameters ["x" "y" "z"]}
                                              {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]}
                    :interface-deps ["auth"]}])

(deftest warnings--when-having-functions-with-same-arity-but-with-different-parameter-lists--return-warnings
  (is (= ["Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
          "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"]
         (illegal-parameters/warnings interfaces components))))

(deftest warnings--when-having-macros-with-same-arity-but-with-different-parameter-lists--return-warnings
  (is (= ["Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
          "Macro in the invoice component is also defined in invoice2 but with a different parameter list: sub.macro1[a], sub.macro1[b]"]
         (illegal-parameters/warnings interfaces2 components2))))

(deftest errors--when-having-duplicated-parameter-lists--return-error
  (is (= ["Duplicated parameter lists found in the payment component: pay[a], pay[b]"]
         (illegal-parameters/errors components))))
