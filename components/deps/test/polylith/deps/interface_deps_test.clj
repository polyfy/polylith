(ns polylith.deps.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.deps.interface :as deps]))

(def interfaces '[{:name "auth", :declarations [{:name add-two, :type function, :signature [x]}], :implementing-components ["auth"]}
                  {:name "invoice"
                   :declarations [{:type data, :name abc}
                                  {:name func1, :type function, :signature [a]}
                                  {:name func1, :type function, :signature [b]}
                                  {:name func1, :type function, :signature [a b]}
                                  {:name func1, :type function, :signature [x y]}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "payment"
                   :declarations [{:name pay, :type function, :signature [a]} {:name pay, :type function, :signature [b]}],
                   :implementing-components ["payment"]}
                  {:name "user"
                   :declarations [{:name func1, :type function, :signature []}
                                  {:name func2, :type function, :signature [a b]}
                                  {:name func2, :type function, :signature [x y]}
                                  {:name func3, :type function, :signature [a b c]}
                                  {:name func3, :type function, :signature [x y z]}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :interface {:name "auth", :declarations [{:name add-two, :type function, :signature [x]}]}
                   :dependencies []}
                  {:name "invoice"
                   :interface {:name "invoice"
                               :declarations [{:type data, :name abc}
                                              {:name func1, :type function, :signature [a]}
                                              {:name func1, :type function, :signature [a b]}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :interface {:name "invoice"
                               :declarations [{:name func1, :type function, :signature [b]}
                                              {:name func1, :type function, :signature [x y]}]}
                   :dependencies []}
                  {:name "payment"
                   :interface {:name "payment"
                               :declarations [{:name pay, :type function, :signature [a]}
                                              {:name pay, :type function, :signature [b]}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :interface {:name "user"
                               :declarations [{:name func1, :type function, :signature []}
                                              {:name func2, :type function, :signature [a b]}
                                              {:name func3, :type function, :signature [a b c]}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :interface {:name "user"
                               :declarations [{:name func2, :type function, :signature [x y]}
                                              {:name func3, :type function, :signature [x y z]}]}
                   :dependencies ["auth"]}])

(deftest interface-deps--a-workspace-with-implementing-deps-from-different-components--should-be-merged-into-a-single-list-of-dependencies
  (is (= '[{:name "auth",
            :declarations [{:name add-two, :type function, :signature [x]}],
            :implementing-components ["auth"],
            :implementing-deps []}
           {:name "invoice",
            :declarations [{:type data, :name abc}
                           {:name func1, :type function, :signature [a]}
                           {:name func1, :type function, :signature [b]}
                           {:name func1, :type function, :signature [a b]}
                           {:name func1, :type function, :signature [x y]}],
            :implementing-components ["invoice" "invoice2"],
            :implementing-deps ["user"]}
           {:name "payment",
            :declarations [{:name pay, :type function, :signature [a]} {:name pay, :type function, :signature [b]}],
            :implementing-components ["payment"],
            :implementing-deps ["invoice"]}
           {:name "user",
            :declarations [{:name func1, :type function, :signature []}
                           {:name func2, :type function, :signature [a b]}
                           {:name func2, :type function, :signature [x y]}
                           {:name func3, :type function, :signature [a b c]}
                           {:name func3, :type function, :signature [x y z]}],
            :implementing-components ["user1" "user2"],
            :implementing-deps ["payment" "auth"]}]
         (deps/interface-deps interfaces components))))
