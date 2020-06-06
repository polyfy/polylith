(ns polylith.workspace.validate.missing-functions
  (:require [clojure.string :as str]))

(def workspace '{:polylith {:top-namespace ""},
                 :components [{:name "auth",
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
                                                          {:name func1, :type function, :signature [a]}
                                                          {:name func1, :type function, :signature [a b]}]},
                               :dependencies ["user"]}
                              {:name "invoice2",
                               :type "component",
                               :imports [{:ns-path "invoice/interface.clj", :imports []} {:ns-path "invoice/core.clj", :imports []}],
                               :interface {:name "invoice",
                                           :declarations [{:name func1, :type function, :signature [b]}
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
                                           :declarations [{:name func1, :type function, :signature []}
                                                          {:name func2, :type function, :signature [a b]}
                                                          {:name func3, :type function, :signature [a b c]}
                                                          {:name func4, :type function, :signature []}]},
                               :dependencies ["payment"]}
                              {:name "user2",
                               :type "component",
                               :imports [{:ns-path "user/interface.clj", :imports []}
                                         {:ns-path "user/core.clj", :imports [auth.interface]}],
                               :interface {:name "user",
                                           :declarations [{:name func2, :type function, :signature [x y]}
                                                          {:name func3, :type function, :signature [x y z]}]},
                               :dependencies ["auth"]}],
                 :bases [],
                 :aliases [],
                 :interfaces [{:name "auth",
                               :declarations [{:name add-two, :type function, :signature [x]}],
                               :implemented-by ["auth"],
                               :dependencies []}
                              {:name "invoice",
                               :type "interface",
                               :declarations [{:type data, :name abc}
                                              {:name func1, :type function, :signature [a]}
                                              {:name func1, :type function, :signature [b]}
                                              {:name func1, :type function, :signature [a b]}
                                              {:name func1, :type function, :signature [x y]}],
                               :implemented-by ["invoice" "invoice2"],
                               :dependencies ["user"]}
                              {:name "payment",
                               :declarations [{:name pay, :type function, :signature [a]} {:name pay, :type function, :signature [b]}],
                               :implemented-by ["payment"],
                               :dependencies ["invoice"]}
                              {:name "user",
                               :type "interface",
                               :declarations [{:name func1, :type function, :signature []}
                                              {:name func2, :type function, :signature [a b]}
                                              {:name func2, :type function, :signature [x y]}
                                              {:name func3, :type function, :signature [a b c]}
                                              {:name func3, :type function, :signature [x y z]}
                                              {:name func4, :type function, :signature []}],
                               :implemented-by ["user1" "user2"],
                               :dependencies ["payment" "auth"]}],
                 :messages {:warnings ["Function in component invoice is also defined in invoice2 but with a different parameter list: func1[a b], func1[x y]"
                                       "Function in component invoice is also defined in invoice2 but with a different parameter list: func1[a], func1[b]"
                                       "Function in component user1 is also defined in user2 but with a different parameter list: func2[a b], func2[x y]"
                                       "Function in component user1 is also defined in user2 but with a different parameter list: func3[a b c], func3[x y z]"],
                            :errors ["Circular dependencies was found: invoice > user > payment > invoice"
                                     "Duplicated signatures found in the payment component: pay[a], pay[b]"]}})

(def interfaces (:interfaces workspace))
(def components (:components workspace))

(defn function->id [{:keys [name signature]}]
  [name (count signature)])

(defn id->functions [{:keys [declarations]}]
  (group-by function->id
            (filterv #(= 'function (:type %)) declarations)))

(defn ->function [{:keys [name signature]}]
  (str name "[" (str/join " " signature) "]"))
















































