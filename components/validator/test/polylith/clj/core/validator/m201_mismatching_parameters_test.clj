(ns polylith.clj.core.validator.m201-mismatching-parameters-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m201-mismatching-parameters :as m201]))

(def interfaces [{:name "auth"
                  :definitions [{:name "add-two", :type "function", :parameters [{:name "x"}]}]
                  :implementing-components ["auth"]
                  :implementing-interface-deps []}
                 {:name "invoice"
                  :type "interface"
                  :definitions [{:name "abc" :type "data"}
                                {:name "func1", :type "function", :parameters [{:name "a"}]}
                                {:name "func1", :type "function", :parameters [{:name "b"}]}
                                {:name "func1", :type "function", :parameters [{:name "a"} {:name "b"}]}
                                {:name "func1", :type "function", :parameters [{:name "x"} {:name "y"}]}]
                  :implementing-components ["invoice" "invoice2"]
                  :implementing-interface-deps ["user"]}
                 {:name "payment"
                  :definitions [{:name "pay", :type "function", :parameters [{:name "a"}]}
                                {:name "pay", :type "function", :parameters [{:name "b"}]}]
                  :implementing-components ["payment"]
                  :implementing-interface-deps ["invoice"]}
                 {:name "user"
                  :type "interface"
                  :definitions [{:name "func1", :type "function", :parameters []}
                                {:name "func2", :type "function", :parameters [{:name "a"} {:name "b"}]}
                                {:name "func2", :type "function", :parameters [{:name "x"} {:name "y"}]}
                                {:name "func3", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"}]}
                                {:name "func3", :type "function", :parameters [{:name "x"} {:name "y"} {:name "z"}]}
                                {:name "func4", :type "function", :parameters []}
                                {:name "func5", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]
                  :implementing-components ["user1" "user2"]
                  :implementing-interface-deps ["payment" "auth"]}])

(def components [{:name "auth"
                  :type "component"
                  :namespaces-src [{:name "auth/interface.clj", :imports ["auth.core"]}
                                   {:name "auth/core.clj", :imports []}]
                  :interface {:name "auth",
                              :definitions [{:name "add-two", :type "function", :parameters [{:name "x"}]}]}
                  :interface-deps []}
                 {:name "invoice"
                  :type "component"
                  :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                   {:name "invoice/core.clj", :imports ["user.interface"]}]
                  :interface {:name "invoice"
                              :definitions [{:name "abc" :type "data"}
                                            {:name "func1", :type "function", :parameters [{:name "a"}]}
                                            {:name "func1", :type "function", :parameters [{:name "a"} {:name "b"}]}]}
                  :interface-deps ["user"]}
                 {:name "invoice2"
                  :type "component"
                  :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                   {:name "invoice/core.clj", :imports []}]
                  :interface {:name "invoice"
                              :definitions [{:name "func1", :type "function", :parameters [{:name "b"}]}
                                            {:name "func1", :type "function", :parameters [{:name "x"} {:name "y"}]}]}
                  :interface-deps []}
                 {:name "payment"
                  :type "component"
                  :namespaces-src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                   {:name "payment/core.clj", :imports ["invoice.interface"]}]
                  :interface {:name "payment"
                              :definitions [{:name "pay", :type "function", :parameters [{:name "a"}]}
                                            {:name "pay", :type "function", :parameters [{:name "b"}]}]}
                  :interface-deps ["invoice"]}
                 {:name "user1"
                  :type "component"
                  :namespaces-src [{:name "user/interface.clj", :imports []}
                                   {:name "user/core.clj", :imports ["payment.interface"]}]
                  :interface {:name "user"
                              :definitions [{:name "func1", :type "function", :parameters []}
                                            {:name "func2", :type "function", :parameters [{:name "a"} {:name "b"}]}
                                            {:name "func3", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"}]}
                                            {:name "func4", :type "function2", :parameters []}
                                            {:name "func5", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                  :interface-deps ["payment"]}
                 {:name "user2"
                  :type "component"
                  :namespaces-src [{:name "user/interface.clj", :imports []}
                                   {:name "user/core.clj", :imports ["auth.interface"]}]
                  :interface {:name "user"
                              :definitions [{:name "func2", :type "function", :parameters [{:name "x"} {:name "y"}]}
                                            {:name "func3", :type "function", :parameters [{:name "x"} {:name "y"} {:name "z"}]}
                                            {:name "func5", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                  :interface-deps ["auth"]}])

(def interfaces2 [{:name "auth",
                   :definitions [{:name "add-two", :type "function", :parameters [{:name "x"}]}]
                   :implementing-components ["auth"]
                   :implementing-interface-deps []}
                  {:name "invoice",
                   :type "interface"
                   :definitions [{:name "abc" :type "data"}
                                 {:name "macro1", :type "macro", :parameters [{:name "a"}]}
                                 {:name "macro1", :type "macro", :parameters [{:name "b"}]}
                                 {:name "func1", :type "function", :parameters [{:name "a"} {:name "b"}]}
                                 {:name "func1", :type "function", :parameters [{:name "x"} {:name "y"}]}]
                   :implementing-components ["invoice" "invoice2"]
                   :implementing-interface-deps ["user"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :parameters [{:name "a"}]}
                                 {:name "pay", :type "function", :parameters [{:name "b"}]}]
                   :implementing-components ["payment"]
                   :implementing-interface-deps ["invoice"]}
                  {:name "user"
                   :type "interface"
                   :definitions [{:name "func1", :type "function", :parameters []}
                                 {:name "func2", :type "function", :parameters [{:name "a"} {:name "b"}]}
                                 {:name "func2", :type "function", :parameters [{:name "x"} {:name "y"}]}
                                 {:name "func3", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"}]}
                                 {:name "func3", :type "function", :parameters [{:name "x"} {:name "y"} {:name "z"}]}
                                 {:name "func4", :type "function", :parameters []}
                                 {:name "func5", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]
                   :implementing-components ["user1" "user2"]
                   :implementing-interface-deps ["payment" "auth"]}])

(def components2 [{:name "auth"
                   :type "component"
                   :namespaces-src [{:name "auth/interface.clj", :imports ["auth.core"]}
                                    {:name "auth/core.clj", :imports []}]
                   :interface {:name "auth"
                               :definitions [{:name "add-two", :type "function", :parameters [{:name "x"}]}]}
                   :interface-deps []}
                  {:name "invoice"
                   :type "component"
                   :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                    {:name "invoice/core.clj", :imports ["user.interface"]}]
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data"}
                                             {:name "macro1", :type "macro", :parameters [{:name "a"}] :sub-ns "sub"}
                                             {:name "func1", :type "function", :parameters [{:name "a"} {:name "b"}]}]}
                   :interface-deps ["user"]}
                  {:name "invoice2"
                   :type "component"
                   :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                    {:name "invoice/core.clj", :imports []}]
                   :interface {:name "invoice"
                               :definitions [{:name "macro1", :type "macro", :parameters [{:name "b"}] :sub-ns "sub"}
                                             {:name "func1", :type "function", :parameters [{:name "x"} {:name "y"}]}]}
                   :interface-deps []}
                  {:name "payment",
                   :type "component"
                   :namespaces-src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                    {:name "payment/core.clj", :imports ["invoice.interface"]}]
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters [{:name "a"}]}
                                             {:name "pay", :type "function", :parameters [{:name "b"}]}]}
                   :interface-deps ["invoice"]}
                  {:name "user1"
                   :type "component"
                   :namespaces-src [{:name "user/interface.clj", :imports []}
                                    {:name "user/core.clj", :imports ["payment.interface"]}]
                   :interface {:name "user"
                               :definitions [{:name "macro1", :type "macro", :parameters []}
                                             {:name "func2", :type "function", :parameters [{:name "a"} {:name "b"}]}
                                             {:name "func3", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"}]}
                                             {:name "func4", :type "function", :parameters []}
                                             {:name "func5", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                   :interface-deps ["payment"]}
                  {:name "user2"
                   :type "component"
                   :namespaces-src [{:name "user/interface.clj", :imports []}
                                    {:name "user/core.clj", :imports ["auth.interface"]}]
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters [{:name "x"} {:name "y"}]}
                                             {:name "func3", :type "function", :parameters [{:name "x"} {:name "y"} {:name "z"}]}
                                             {:name "func5", :type "function", :parameters [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                   :interface-deps ["auth"]}])

(def interfaces3 [{:name "auth",
                   :definitions [{:name "hello", :type "function", :parameters [{:name "x"}]}]
                   :implementing-components ["auth1" "auth2"]
                   :implementing-interface-deps []}])

(def components3 [{:name "auth1"
                   :type "component"
                   :interface {:name "auth"
                               :definitions [{:name "hello", :type "function", :parameters [{:name "x"}]}]}
                   :interface-deps []}
                  {:name "auth2"
                   :type "component"
                   :interface {:name "auth"
                               :definitions [{:name "hello", :type "function", :parameters [{:name "x", :type "^String"}]}]}
                   :interface-deps []}])

(deftest warnings--when-having-functions-with-the-same-arity-but-with-different-parameter-lists--return-warnings
         (is (= [{:type "warning"
                  :code 201
                  :colorized-message "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                  :message           "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                  :components ["invoice" "invoice2"]}
                 {:type "warning"
                  :code 201
                  :colorized-message "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
                  :message           "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
                  :components ["invoice" "invoice2"]}
                 {:type "warning"
                  :code 201
                  :colorized-message "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                  :message           "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                  :components ["user1" "user2"]}
                 {:type "warning"
                  :code 201
                  :colorized-message "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
                  :message           "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
                  :components ["user1" "user2"]}]
                (sort-by :message
                         (m201/warnings interfaces components color/none)))))

(deftest warnings--when-having-macros-with-the-same-arity-but-with-different-parameter-lists--return-warnings
         (is (= [{:type "warning"
                  :code 201
                  :colorized-message "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                  :message           "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                  :components ["invoice" "invoice2"]}
                 {:type "warning"
                  :code 201
                  :colorized-message "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                  :message           "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                  :components ["user1" "user2"]}
                 {:type "warning"
                  :code 201
                  :colorized-message "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
                  :message           "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
                  :components ["user1" "user2"]}
                 {:type "warning"
                  :code 201
                  :colorized-message "Macro in the invoice component is also defined in invoice2 but with a different parameter list: sub.macro1[a], sub.macro1[b]"
                  :message           "Macro in the invoice component is also defined in invoice2 but with a different parameter list: sub.macro1[a], sub.macro1[b]"
                  :components ["invoice" "invoice2"]}]
                (sort-by :message
                         (m201/warnings interfaces2 components2 color/none)))))

(deftest warnings--when-having-functions-with-the-same-arity-but-with-a-type-hint-in-only-one-of-the-parameter-lists--return-warning
  (is (= [{:type "warning"
           :code 201
           :colorized-message "Function in the auth1 component is also defined in auth2 but with a different parameter list: hello[^String x], hello[x]"
           :message "Function in the auth1 component is also defined in auth2 but with a different parameter list: hello[^String x], hello[x]"
           :components ["auth1" "auth2"]}]
         (sort-by :message
                  (m201/warnings interfaces3 components3 color/none)))))
