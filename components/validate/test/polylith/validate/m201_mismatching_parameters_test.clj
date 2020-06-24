(ns polylith.validate.m201-mismatching-parameters-test
  (:require [clojure.test :refer :all]
            [polylith.validate.m201-mismatching-parameters :as m201]))

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
                   :namespaces-src [{:name "auth/interface.clj", :imports ["auth.core"]}
                                    {:name "auth/core.clj", :imports []}]
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"]}]}
                   :interface-deps []}
                  {:name "invoice"
                   :type "component"
                   :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                    {:name "invoice/core.clj", :imports ["user.interface"]}]
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data"}
                                             {:name "func1", :type "function", :parameters ["a"]}
                                             {:name "func1", :type "function", :parameters ["a" "b"]}]}
                   :interface-deps ["user"]}
                  {:name "invoice2"
                   :type "component"
                   :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                    {:name "invoice/core.clj", :imports []}]
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"]}
                                             {:name "func1", :type "function", :parameters ["x" "y"]}]}
                   :interface-deps []}
                  {:name "payment"
                   :type "component"
                   :namespaces-src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                    {:name "payment/core.clj", :imports ["invoice.interface"]}]
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"]}
                                             {:name "pay", :type "function", :parameters ["b"]}]}
                   :interface-deps ["invoice"]}
                  {:name "user1"
                   :type "component"
                   :namespaces-src [{:name "user/interface.clj", :imports []}
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
                   :namespaces-src [{:name "user/interface.clj", :imports []}
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
                    :namespaces-src [{:name "auth/interface.clj", :imports ["auth.core"]}
                                     {:name "auth/core.clj", :imports []}]
                    :interface {:name "auth"
                                :definitions [{:name "add-two", :type "function", :parameters ["x"]}]}
                    :interface-deps []}
                   {:name "invoice"
                    :type "component"
                    :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                     {:name "invoice/core.clj", :imports ["user.interface"]}]
                    :interface {:name "invoice"
                                :definitions [{:name "abc" :type "data"}
                                              {:name "macro1", :type "macro", :parameters ["a"] :sub-ns "sub"}
                                              {:name "func1", :type "function", :parameters ["a" "b"]}]}
                    :interface-deps ["user"]}
                   {:name "invoice2"
                    :type "component"
                    :namespaces-src [{:name "invoice/interface.clj", :imports []}
                                     {:name "invoice/core.clj", :imports []}]
                    :interface {:name "invoice"
                                :definitions [{:name "macro1", :type "macro", :parameters ["b"] :sub-ns "sub"}
                                              {:name "func1", :type "function", :parameters ["x" "y"]}]}
                    :interface-deps []}
                   {:name "payment",
                    :type "component"
                    :namespaces-src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                     {:name "payment/core.clj", :imports ["invoice.interface"]}]
                    :interface {:name "payment"
                                :definitions [{:name "pay", :type "function", :parameters ["a"]}
                                              {:name "pay", :type "function", :parameters ["b"]}]}
                    :interface-deps ["invoice"]}
                   {:name "user1"
                    :type "component"
                    :namespaces-src [{:name "user/interface.clj", :imports []}
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
                    :namespaces-src [{:name "user/interface.clj", :imports []}
                                     {:name "user/core.clj", :imports ["auth.interface"]}]
                    :interface {:name "user"
                                :definitions [{:name "func2", :type "function", :parameters ["x" "y"]}
                                              {:name "func3", :type "function", :parameters ["x" "y" "z"]}
                                              {:name "func5", :type "function", :parameters ["a" "b" "c" "d"]}]}
                    :interface-deps ["auth"]}])

(deftest warnings--when-having-functions-with-same-arity-but-with-different-parameter-lists--return-warnings
         (is (= [{:type "warning"
                  :code 201
                  :message "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                  :components ["invoice" "invoice2"]}
                 {:type "warning"
                  :code 201
                  :message "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
                  :components ["invoice" "invoice2"]}
                 {:type "warning"
                  :code 201
                  :message "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                  :components ["user1" "user2"]}
                 {:type "warning"
                  :code 201
                  :message "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
                  :components ["user1" "user2"]}]
                (sort-by :message
                         (mapv #(select-keys % [:type :code :message :components])
                               (m201/warnings interfaces components))))))

(deftest warnings--when-having-macros-with-same-arity-but-with-different-parameter-lists--return-warnings
         (is (= [{:type "warning"
                  :code 201
                  :message "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                  :components ["invoice" "invoice2"]}
                 {:type "warning"
                  :code 201
                  :message "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                  :components ["user1" "user2"]}
                 {:type "warning"
                  :code 201
                  :message "Function in the user1 component is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
                  :components ["user1" "user2"]}
                 {:type "warning"
                  :code 201
                  :message "Macro in the invoice component is also defined in invoice2 but with a different parameter list: sub.macro1[a], sub.macro1[b]"
                  :components ["invoice" "invoice2"]}]
                (sort-by :message
                         (mapv #(select-keys % [:type :code :message :components])
                               (m201/warnings interfaces2 components2))))))
