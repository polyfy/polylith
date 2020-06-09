(ns polylith.deps.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.deps.interface :as deps]))

(def interfaces '[{:name "auth", :definitions [{:name add-two, :type function, :parameters [x]}], :implementing-components ["auth"]}
                  {:name "invoice"
                   :definitions [{:type definition, :name abc}
                                 {:name func1, :type function, :parameters [a]}
                                 {:name func1, :type function, :parameters [b]}
                                 {:name func1, :type function, :parameters [a b]}
                                 {:name func1, :type function, :parameters [x y]}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "payment"
                   :definitions [{:name pay, :type function, :parameters [a]} {:name pay, :type function, :parameters [b]}],
                   :implementing-components ["payment"]}
                  {:name "user"
                   :definitions [{:name func1, :type function, :parameters []}
                                 {:name func2, :type function, :parameters [a b]}
                                 {:name func2, :type function, :parameters [x y]}
                                 {:name func3, :type function, :parameters [a b c]}
                                 {:name func3, :type function, :parameters [x y z]}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :interface {:name "auth", :definitions [{:name add-two, :type function, :parameters [x]}]}
                   :dependencies []}
                  {:name "invoice"
                   :interface {:name "invoice"
                               :definitions [{:type definition, :name abc}
                                             {:name func1, :type function, :parameters [a]}
                                             {:name func1, :type function, :parameters [a b]}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :interface {:name "invoice"
                               :definitions [{:name func1, :type function, :parameters [b]}
                                             {:name func1, :type function, :parameters [x y]}]}
                   :dependencies []}
                  {:name "payment"
                   :interface {:name "payment"
                               :definitions [{:name pay, :type function, :parameters [a]}
                                             {:name pay, :type function, :parameters [b]}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :interface {:name "user"
                               :definitions [{:name func1, :type function, :parameters []}
                                             {:name func2, :type function, :parameters [a b]}
                                             {:name func3, :type function, :parameters [a b c]}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :interface {:name "user"
                               :definitions [{:name func2, :type function, :parameters [x y]}
                                             {:name func3, :type function, :parameters [x y z]}]}
                   :dependencies ["auth"]}])

(deftest interface-deps--a-workspace-with-implementing-deps-from-different-components--should-be-merged-into-a-single-list-of-dependencies
  (is (= '[{:name "auth",
            :definitions [{:name add-two, :type function, :parameters [x]}],
            :implementing-components ["auth"],
            :implementing-deps []}
           {:name "invoice",
            :definitions [{:type definition, :name abc}
                          {:name func1, :type function, :parameters [a]}
                          {:name func1, :type function, :parameters [b]}
                          {:name func1, :type function, :parameters [a b]}
                          {:name func1, :type function, :parameters [x y]}],
            :implementing-components ["invoice" "invoice2"],
            :implementing-deps ["user"]}
           {:name "payment",
            :definitions [{:name pay, :type function, :parameters [a]} {:name pay, :type function, :parameters [b]}],
            :implementing-components ["payment"],
            :implementing-deps ["invoice"]}
           {:name "user",
            :definitions [{:name func1, :type function, :parameters []}
                          {:name func2, :type function, :parameters [a b]}
                          {:name func2, :type function, :parameters [x y]}
                          {:name func3, :type function, :parameters [a b c]}
                          {:name func3, :type function, :parameters [x y z]}],
            :implementing-components ["user1" "user2"],
            :implementing-deps ["payment" "auth"]}]
         (deps/interface-deps interfaces components))))
