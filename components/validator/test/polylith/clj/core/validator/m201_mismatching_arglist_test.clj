(ns polylith.clj.core.validator.m201-mismatching-arglist-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m201-mismatching-argument-lists :as m201]))

(def interfaces [{:name "auth"
                  :definitions [{:name "add-two", :type "function", :arglist [{:name "x"}]}]
                  :implementing-components ["auth"]}
                 {:name "invoice"
                  :type "interface"
                  :definitions [{:name "abc" :type "data"}
                                {:name "func1", :type "function", :arglist [{:name "a"}]}
                                {:name "func1", :type "function", :arglist [{:name "b"}]}
                                {:name "func1", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]
                  :implementing-components ["invoice" "invoice2"]}
                 {:name "payment"
                  :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                {:name "pay", :type "function", :arglist [{:name "b"}]}]
                  :implementing-components ["payment"]}
                 {:name "user"
                  :type "interface"
                  :definitions [{:name "func1", :type "function", :arglist []}
                                {:name "func2", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                {:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                {:name "func3", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"}]}
                                {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                {:name "func4", :type "function", :arglist []}
                                {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]
                  :implementing-components ["user1" "user2"]}])

(def components [{:name "auth"
                  :type "component"
                  :namespaces {:src [{:name "auth/interface.clj", :imports ["auth.core"]}
                                     {:name "auth/core.clj", :imports []}]}
                  :interface {:name "auth",
                              :definitions [{:name "add-two", :type "function", :arglist [{:name "x"}]}]}
                  :interface-deps {}}
                 {:name "invoice"
                  :type "component"
                  :namespaces {:src [{:name "invoice/interface.clj", :imports []}
                                     {:name "invoice/core.clj", :imports ["user.interface"]}]}
                  :interface {:name "invoice"
                              :definitions [{:name "abc" :type "data"}
                                            {:name "func1", :type "function", :arglist [{:name "a"}]}
                                            {:name "func1", :type "function", :arglist [{:name "a"} {:name "b"}]}]}
                  :interface-deps {:src ["user"]}}
                 {:name "invoice2"
                  :type "component"
                  :namespaces {:src [{:name "invoice/interface.clj", :imports []}
                                     {:name "invoice/core.clj", :imports []}]}
                  :interface {:name "invoice"
                              :definitions [{:name "func1", :type "function", :arglist [{:name "b"}]}
                                            {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]}
                  :interface-deps {}}
                 {:name "payment"
                  :type "component"
                  :namespaces {:src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                     {:name "payment/core.clj", :imports ["invoice.interface"]}]}
                  :interface {:name "payment"
                              :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                            {:name "pay", :type "function", :arglist [{:name "b"}]}]}
                  :interface-deps {:src ["invoice"]}}
                 {:name "user1"
                  :type "component"
                  :namespaces {:src [{:name "user/interface.clj", :imports []}
                                     {:name "user/core.clj", :imports ["payment.interface"]}]}
                  :interface {:name "user"
                              :definitions [{:name "func1", :type "function", :arglist []}
                                            {:name "func2", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                            {:name "func3", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"}]}
                                            {:name "func4", :type "function2", :arglist []}
                                            {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                  :interface-deps {:src ["payment"]}}
                 {:name "user2"
                  :type "component"
                  :namespaces {:src [{:name "user/interface.clj", :imports []}
                                     {:name "user/core.clj", :imports ["auth.interface"]}]}
                  :interface {:name "user"
                              :definitions [{:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                            {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                            {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                  :interface-deps {:src ["auth"]}}])

(def interfaces2 [{:name "auth",
                   :definitions [{:name "add-two", :type "function", :arglist [{:name "x"}]}]
                   :implementing-components ["auth"]}
                  {:name "invoice",
                   :type "interface"
                   :definitions [{:name "abc" :type "data"}
                                 {:name "macro1", :type "macro", :arglist [{:name "a"}]}
                                 {:name "macro1", :type "macro", :arglist [{:name "b"}]}
                                 {:name "func1", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                 {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                 {:name "pay", :type "function", :arglist [{:name "b"}]}]
                   :implementing-components ["payment"]}
                  {:name "user"
                   :type "interface"
                   :definitions [{:name "func1", :type "function", :arglist []}
                                 {:name "func2", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                 {:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                 {:name "func3", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"}]}
                                 {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                 {:name "func4", :type "function", :arglist []}
                                 {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]
                   :implementing-components ["user1" "user2"]}])

(def components2 [{:name "auth"
                   :type "component"
                   :namespaces {:src [{:name "auth/interface.clj", :imports ["auth.core"]}
                                      {:name "auth/core.clj", :imports []}]}
                   :interface {:name "auth"
                               :definitions [{:name "add-two", :type "function", :arglist [{:name "x"}]}]}
                   :interface-deps {}}
                  {:name "invoice"
                   :type "component"
                   :namespaces {:src [{:name "invoice/interface.clj", :imports []}
                                      {:name "invoice/core.clj", :imports ["user.interface"]}]}
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data"}
                                             {:name "macro1", :type "macro", :arglist [{:name "a"}] :sub-ns "sub"}
                                             {:name "func1", :type "function", :arglist [{:name "a"} {:name "b"}]}]}
                   :interface-deps {:src ["user"]}}
                  {:name "invoice2"
                   :type "component"
                   :namespaces {:src [{:name "invoice/interface.clj", :imports []}
                                      {:name "invoice/core.clj", :imports []}]}
                   :interface {:name "invoice"
                               :definitions [{:name "macro1", :type "macro", :arglist [{:name "b"}] :sub-ns "sub"}
                                             {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]}
                   :interface-deps {}}
                  {:name "payment",
                   :type "component"
                   :namespaces {:src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                      {:name "payment/core.clj", :imports ["invoice.interface"]}]}
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                             {:name "pay", :type "function", :arglist [{:name "b"}]}]}
                   :interface-deps {:src ["invoice"]}}
                  {:name "user1"
                   :type "component"
                   :namespaces {:src [{:name "user/interface.clj", :imports []}
                                      {:name "user/core.clj", :imports ["payment.interface"]}]}
                   :interface {:name "user"
                               :definitions [{:name "macro1", :type "macro", :arglist []}
                                             {:name "func2", :type "function", :arglist [{:name "a"} {:name "b"}]}
                                             {:name "func3", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"}]}
                                             {:name "func4", :type "function", :arglist []}
                                             {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                   :interface-deps {:src ["payment"]}}
                  {:name "user2"
                   :type "component"
                   :namespaces {:src [{:name "user/interface.clj", :imports []}
                                      {:name "user/core.clj", :imports ["auth.interface"]}]}
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                             {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                             {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                   :interface-deps {:src ["auth"]}}])

(def interfaces3 [{:name "auth",
                   :definitions [{:name "hello", :type "function", :arglist [{:name "x"}]}]
                   :implementing-components ["auth1" "auth2"]}])

(def components3 [{:name "auth1"
                   :type "component"
                   :interface {:name "auth"
                               :definitions [{:name "hello", :type "function", :arglist [{:name "x"}]}]}
                   :interface-deps {}}
                  {:name "auth2"
                   :type "component"
                   :interface {:name "auth"
                               :definitions [{:name "hello", :type "function", :arglist [{:name "x", :type "^String"}]}]}
                   :interface-deps {}}])

(deftest warnings--when-having-functions-with-the-same-arity-but-with-different-arglists--return-warnings
  (is (= (sort-by :message
                  (m201/warnings interfaces components color/none))
         [{:type "warning"
           :code 201
           :colorized-message "Function in the invoice component is also defined in invoice2 but with a different argument list: func1[a b], func1[x y]"
           :message           "Function in the invoice component is also defined in invoice2 but with a different argument list: func1[a b], func1[x y]"
           :components ["invoice" "invoice2"]}
          {:type "warning"
           :code 201
           :colorized-message "Function in the invoice component is also defined in invoice2 but with a different argument list: func1[a], func1[b]"
           :message           "Function in the invoice component is also defined in invoice2 but with a different argument list: func1[a], func1[b]"
           :components ["invoice" "invoice2"]}
          {:type "warning"
           :code 201
           :colorized-message "Function in the user1 component is also defined in user2 but with a different argument list: func2[a b], func2[x y]"
           :message           "Function in the user1 component is also defined in user2 but with a different argument list: func2[a b], func2[x y]"
           :components ["user1" "user2"]}
          {:type "warning"
           :code 201
           :colorized-message "Function in the user1 component is also defined in user2 but with a different argument list: func3[a b c], func3[x y z]"
           :message           "Function in the user1 component is also defined in user2 but with a different argument list: func3[a b c], func3[x y z]"
           :components ["user1" "user2"]}])))

(deftest warnings--when-having-macros-with-the-same-arity-but-with-different-argument-lists--return-warnings
  (is (= (sort-by :message
                  (m201/warnings interfaces2 components2 color/none))
         [{:type "warning"
           :code 201
           :colorized-message "Function in the invoice component is also defined in invoice2 but with a different argument list: func1[a b], func1[x y]"
           :message           "Function in the invoice component is also defined in invoice2 but with a different argument list: func1[a b], func1[x y]"
           :components ["invoice" "invoice2"]}
          {:type "warning"
           :code 201
           :colorized-message "Function in the user1 component is also defined in user2 but with a different argument list: func2[a b], func2[x y]"
           :message           "Function in the user1 component is also defined in user2 but with a different argument list: func2[a b], func2[x y]"
           :components ["user1" "user2"]}
          {:type "warning"
           :code 201
           :colorized-message "Function in the user1 component is also defined in user2 but with a different argument list: func3[a b c], func3[x y z]"
           :message           "Function in the user1 component is also defined in user2 but with a different argument list: func3[a b c], func3[x y z]"
           :components ["user1" "user2"]}
          {:type "warning"
           :code 201
           :colorized-message "Macro in the invoice component is also defined in invoice2 but with a different argument list: sub.macro1[a], sub.macro1[b]"
           :message           "Macro in the invoice component is also defined in invoice2 but with a different argument list: sub.macro1[a], sub.macro1[b]"
           :components ["invoice" "invoice2"]}])))

(deftest warnings--when-having-functions-with-the-same-arity-but-with-a-type-hint-in-only-one-of-the-argument-lists--return-warning
  (is (= (sort-by :message
                  (m201/warnings interfaces3 components3 color/none))
         [{:type "warning"
           :code 201
           :colorized-message "Function in the auth1 component is also defined in auth2 but with a different argument list: hello[^String x], hello[x]"
           :message "Function in the auth1 component is also defined in auth2 but with a different argument list: hello[^String x], hello[x]"
           :components ["auth1" "auth2"]}])))
