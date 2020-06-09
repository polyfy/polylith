(ns polylith.validate.illegal-signatures-test
  (:require [clojure.test :refer :all]
            [polylith.validate.illegal-signatures :as illegal-signatures]))

(def interfaces '[{:name "auth"
                   :declarations [{:name add-two, :type function, :signature [x]}]
                   :implementing-components ["auth"]
                   :implementing-deps []}
                  {:name "invoice"
                   :type "interface"
                   :declarations [{:type data, :name abc}
                                  {:name func1, :type function, :signature [a]}
                                  {:name func1, :type function, :signature [b]}
                                  {:name func1, :type function, :signature [a b]}
                                  {:name func1, :type function, :signature [x y]}]
                   :implementing-components ["invoice" "invoice2"]
                   :implementing-deps ["user"]}
                  {:name "payment"
                   :declarations [{:name pay, :type function, :signature [a]} {:name pay, :type function, :signature [b]}]
                   :implementing-components ["payment"]
                   :implementing-deps ["invoice"]}
                  {:name "user"
                   :type "interface"
                   :declarations [{:name func1, :type function, :signature []}
                                  {:name func2, :type function, :signature [a b]}
                                  {:name func2, :type function, :signature [x y]}
                                  {:name func3, :type function, :signature [a b c]}
                                  {:name func3, :type function, :signature [x y z]}
                                  {:name func4, :type function, :signature []}
                                  {:name func5, :type function, :signature [a b c d]}]
                   :implementing-components ["user1" "user2"]
                   :implementing-deps ["payment" "auth"]}])

(def components '[{:name "auth"
                   :type "component"
                   :imports [{:ns-path "auth/interface.clj", :imports [auth.core]} {:ns-path "auth/core.clj", :imports []}]
                   :interface {:name "auth", :declarations [{:name add-two, :type function, :signature [x]}]}
                   :dependencies []}
                  {:name "invoice"
                   :type "component"
                   :imports [{:ns-path "invoice/interface.clj", :imports []}
                             {:ns-path "invoice/core.clj", :imports [user.interface]}]
                   :interface {:name "invoice"
                               :declarations [{:type data, :name abc}
                                              {:name func1, :type function, :signature [a]}
                                              {:name func1, :type function, :signature [a b]}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :type "component"
                   :imports [{:ns-path "invoice/interface.clj", :imports []} {:ns-path "invoice/core.clj", :imports []}]
                   :interface {:name "invoice"
                               :declarations [{:name func1, :type function, :signature [b]}
                                              {:name func1, :type function, :signature [x y]}]}
                   :dependencies []}
                  {:name "payment"
                   :type "component"
                   :imports [{:ns-path "payment/interface.clj", :imports [payment.core]}
                             {:ns-path "payment/core.clj", :imports [invoice.interface]}]
                   :interface {:name "payment"
                               :declarations [{:name pay, :type function, :signature [a]}
                                              {:name pay, :type function, :signature [b]}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :type "component"
                   :imports [{:ns-path "user/interface.clj", :imports []}
                             {:ns-path "user/core.clj", :imports [payment.interface]}]
                   :interface {:name "user"
                               :declarations [{:name func1, :type function, :signature []}
                                              {:name func2, :type function, :signature [a b]}
                                              {:name func3, :type function, :signature [a b c]}
                                              {:name func4, :type function, :signature []}
                                              {:name func5, :type function, :signature [a b c d]}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :type "component"
                   :imports [{:ns-path "user/interface.clj", :imports []}
                             {:ns-path "user/core.clj", :imports [auth.interface]}]
                   :interface {:name "user"
                               :declarations [{:name func2, :type function, :signature [x y]}
                                              {:name func3, :type function, :signature [x y z]}
                                              {:name func5, :type function, :signature [a b c d]}]}
                   :dependencies ["auth"]}])

(def interfaces2 '[{:name "auth",
                    :declarations [{:name add-two, :type function, :signature [x]}],
                    :implementing-components ["auth"],
                    :implementing-deps []}
                   {:name "invoice",
                    :type "interface",
                    :declarations [{:type data, :name abc}
                                   {:name func1, :type macro, :signature [a]}
                                   {:name func1, :type macro, :signature [b]}
                                   {:name func1, :type function, :signature [a b]}
                                   {:name func1, :type function, :signature [x y]}],
                    :implementing-components ["invoice" "invoice2"],
                    :implementing-deps ["user"]}
                   {:name "payment",
                    :declarations [{:name pay, :type function, :signature [a]} {:name pay, :type function, :signature [b]}],
                    :implementing-components ["payment"],
                    :implementing-deps ["invoice"]}
                   {:name "user",
                    :type "interface",
                    :declarations [{:name func1, :type function, :signature []}
                                   {:name func2, :type function, :signature [a b]}
                                   {:name func2, :type function, :signature [x y]}
                                   {:name func3, :type function, :signature [a b c]}
                                   {:name func3, :type function, :signature [x y z]}
                                   {:name func4, :type function, :signature []}
                                   {:name func5, :type function, :signature [a b c d]}],
                    :implementing-components ["user1" "user2"],
                    :implementing-deps ["payment" "auth"]}])

(def components2 '[{:name "auth",
                    :type "component",
                    :imports [{:ns-path "auth/interface.clj", :imports [auth.core]} {:ns-path "auth/core.clj", :imports []}],
                    :interface {:name "auth", :declarations [{:name add-two, :type function, :signature [x]}]},
                    :dependencies []}
                   {:name "invoice",
                    :type "component",
                    :imports [{:ns-path "invoice/interface.clj", :imports []}
                              {:ns-path "invoice/core.clj", :imports [user.interface]}],
                    :interface {:name "invoice",
                                :declarations [{:type data, :name abc}
                                               {:name func1, :type macro, :signature [a]}
                                               {:name func1, :type function, :signature [a b]}]},
                    :dependencies ["user"]}
                   {:name "invoice2",
                    :type "component",
                    :imports [{:ns-path "invoice/interface.clj", :imports []} {:ns-path "invoice/core.clj", :imports []}],
                    :interface {:name "invoice",
                                :declarations [{:name func1, :type macro, :signature [b]}
                                               {:name func1, :type function, :signature [x y]}]},
                    :dependencies []}
                   {:name "payment",
                    :type "component",
                    :imports [{:ns-path "payment/interface.clj", :imports [payment.core]}
                              {:ns-path "payment/core.clj", :imports [invoice.interface]}],
                    :interface {:name "payment",
                                :declarations [{:name pay, :type function, :signature [a]}
                                               {:name pay, :type function, :signature [b]}]},
                    :dependencies ["invoice"]}
                   {:name "user1",
                    :type "component",
                    :imports [{:ns-path "user/interface.clj", :imports []}
                              {:ns-path "user/core.clj", :imports [payment.interface]}],
                    :interface {:name "user",
                                :declarations [{:name func1, :type macro, :signature []}
                                               {:name func2, :type function, :signature [a b]}
                                               {:name func3, :type function, :signature [a b c]}
                                               {:name func4, :type function, :signature []}
                                               {:name func5, :type function, :signature [a b c d]}]},
                    :dependencies ["payment"]}
                   {:name "user2",
                    :type "component",
                    :imports [{:ns-path "user/interface.clj", :imports []}
                              {:ns-path "user/core.clj", :imports [auth.interface]}],
                    :interface {:name "user",
                                :declarations [{:name func2, :type function, :signature [x y]}
                                               {:name func3, :type function, :signature [x y z]}
                                               {:name func5, :type function, :signature [a b c d]}]},
                    :dependencies ["auth"]}])

(deftest warnings--when-have-functions-with-same-arity-but-with-different-parameter-lists--return-warnings
  (is (= ["Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
          "Function in the invoice component is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
          "Function in the user1 component is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
          (illegal-signatures/warnings interfaces components)])))

(deftest warnings--when-have-macros-with-same-arity-but-with-different-parameter-lists--return-warnings
  (is (= ["Function in invoice is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
          "Function in user1 is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
          "Function in user1 is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"
          "Macro in invoice is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
          (illegal-signatures/warnings interfaces2 components2)])))

(deftest errors--when-have-duplicated-signatures--return-error
  (is (= ["Duplicated signatures found in the payment component: pay[a], pay[b]"]
         (illegal-signatures/errors components))))
