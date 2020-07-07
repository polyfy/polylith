(ns polylith.clj.core.validate.m102-duplicated-parameter-lists-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validate.m102-duplicated-parameter-lists :as m102]))

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

(deftest errors--when-having-duplicated-parameter-lists--return-error
  (is (= [{:type "error",
           :code 102,
           :message "Duplicated parameter lists found in payment: pay[a], pay[b]",
           :colorized-message "Duplicated parameter lists found in payment: pay[a], pay[b]",
           :components ["payment"]}]
         (m102/errors components color/none))))
