(ns polylith.clj.core.validator.m102-function-or-macro-is-defined-twice-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m102-function-or-macro-is-defined-twice :as m102]))

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
                  :interface-deps ["user"]}
                 {:name "invoice2"
                  :type "component"
                  :namespaces {:src [{:name "invoice/interface.clj", :imports []}
                                     {:name "invoice/core.clj", :imports []}]}
                  :interface {:name "invoice"
                              :definitions [{:name "func1", :type "function", :arglist [{:name "b"}]}
                                            {:name "func1", :type "function", :arglist [{:name "x"} {:name "y"}]}]}
                  :interface-deps []}
                 {:name "payment"
                  :type "component"
                  :namespaces {:src [{:name "payment/interface.clj", :imports ["payment.core"]}
                                     {:name "payment/core.clj", :imports ["invoice.interface"]}]}
                  :interface {:name "payment"
                              :definitions [{:name "pay", :type "function", :arglist [{:name "a"}]}
                                            {:name "pay", :type "function", :arglist [{:name "b"}]}]}
                  :interface-deps ["invoice"]}
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
                  :interface-deps ["payment"]}
                 {:name "user2"
                  :type "component"
                  :namespaces {:src [{:name "user/interface.clj", :imports []}
                                     {:name "user/core.clj", :imports ["auth.interface"]}]}
                  :interface {:name "user"
                              :definitions [{:name "func2", :type "function", :arglist [{:name "x"} {:name "y"}]}
                                            {:name "func3", :type "function", :arglist [{:name "x"} {:name "y"} {:name "z"}]}
                                            {:name "func5", :type "function", :arglist [{:name "a"} {:name "b"} {:name "c"} {:name "d"}]}]}
                  :interface-deps ["auth"]}])

(deftest errors--when-having-duplicated-arglists--return-error
  (is (= (m102/errors components color/none)
         [{:type "error",
           :code 102,
           :message "Function or macro is defined twice in payment: pay[a], pay[b]",
           :colorized-message "Function or macro is defined twice in payment: pay[a], pay[b]",
           :components ["payment"]}])))
