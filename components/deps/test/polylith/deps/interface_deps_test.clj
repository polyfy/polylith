(ns polylith.deps.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.deps.interface :as deps]))

(def interfaces '[{:name "auth",
                   :definitions [{:name "add-two", :type "function", :parameters ["x"]}], :implementing-components ["auth"]}
                  {:name "invoice"
                   :definitions [{:name "abc" :type "data" :ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["b"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}
                                 {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                 {:name "pay", :type "function", :parameters ["b"] :ns ""}],
                   :implementing-components ["payment"]}
                  {:name "user"
                   :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                 {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                 {:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"] :ns ""}]}
                   :dependencies []}
                  {:name "invoice"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :ns ""}
                                             {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"] :ns ""}
                                             {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]}
                   :dependencies []}
                  {:name "payment"
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                                             {:name "pay", :type "function", :parameters ["b"] :ns ""}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                                             {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}]}
                   :dependencies ["auth"]}])

(deftest interface-deps--a-workspace-with-implementing-deps-from-different-components--should-be-merged-into-a-single-list-of-dependencies
  (is (= '[{:name "auth"
            :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
            :implementing-components ["auth"]
            :implementing-deps []}
           {:name "invoice"
            :definitions [{:name "abc" :type "data" :ns ""}
                          {:name "func1", :type "function", :parameters ["a"] :ns ""}
                          {:name "func1", :type "function", :parameters ["b"] :ns ""}
                          {:name "func1", :type "function", :parameters ["a" "b"] :ns ""}
                          {:name "func1", :type "function", :parameters ["x" "y"] :ns ""}]
            :implementing-components ["invoice" "invoice2"]
            :implementing-deps ["user"]}
           {:name "payment",
            :definitions [{:name "pay", :type "function", :parameters ["a"] :ns ""}
                          {:name "pay", :type "function", :parameters ["b"] :ns ""}]
            :implementing-components ["payment"],
            :implementing-deps ["invoice"]}
           {:name "user",
            :definitions [{:name "func1", :type "function", :parameters [] :ns ""}
                          {:name "func2", :type "function", :parameters ["a" "b"] :ns ""}
                          {:name "func2", :type "function", :parameters ["x" "y"] :ns ""}
                          {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns ""}
                          {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns ""}]
            :implementing-components ["user1" "user2"]
            :implementing-deps ["payment" "auth"]}]
         (deps/interface-deps interfaces components))))
