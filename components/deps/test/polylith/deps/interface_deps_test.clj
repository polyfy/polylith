(ns polylith.deps.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.deps.interface :as deps]))

(def interfaces '[{:name "auth",
                   :definitions [{:name "add-two", :type "function", :parameters ["x"]}], :implementing-components ["auth"]}
                  {:name "invoice"
                   :definitions [{:name "abc" :type "data" :ns "interface"}
                                 {:name "func1", :type "function", :parameters ["a"] :ns "interface"}
                                 {:name "func1", :type "function", :parameters ["b"] :ns "interface"}
                                 {:name "func1", :type "function", :parameters ["a" "b"] :ns "interface"}
                                 {:name "func1", :type "function", :parameters ["x" "y"] :ns "interface"}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"] :ns "interface"}
                                 {:name "pay", :type "function", :parameters ["b"] :ns "interface"}],
                   :implementing-components ["payment"]}
                  {:name "user"
                   :definitions [{:name "func1", :type "function", :parameters [] :ns "interface"}
                                 {:name "func2", :type "function", :parameters ["a" "b"] :ns "interface"}
                                 {:name "func2", :type "function", :parameters ["x" "y"] :ns "interface"}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns "interface"}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns "interface"}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"] :ns "interface"}]}
                   :dependencies []}
                  {:name "invoice"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :ns "interface"}
                                             {:name "func1", :type "function", :parameters ["a"] :ns "interface"}
                                             {:name "func1", :type "function", :parameters ["a" "b"] :ns "interface"}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"] :ns "interface"}
                                             {:name "func1", :type "function", :parameters ["x" "y"] :ns "interface"}]}
                   :dependencies []}
                  {:name "payment"
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"] :ns "interface"}
                                             {:name "pay", :type "function", :parameters ["b"] :ns "interface"}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters [] :ns "interface"}
                                             {:name "func2", :type "function", :parameters ["a" "b"] :ns "interface"}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns "interface"}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :ns "interface"}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns "interface"}]}
                   :dependencies ["auth"]}])

(deftest interface-deps--a-workspace-with-implementing-deps-from-different-components--should-be-merged-into-a-single-list-of-dependencies
  (is (= '[{:name "auth"
            :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
            :implementing-components ["auth"]
            :implementing-deps []}
           {:name "invoice"
            :definitions [{:name "abc" :type "data" :ns "interface"}
                          {:name "func1", :type "function", :parameters ["a"] :ns "interface"}
                          {:name "func1", :type "function", :parameters ["b"] :ns "interface"}
                          {:name "func1", :type "function", :parameters ["a" "b"] :ns "interface"}
                          {:name "func1", :type "function", :parameters ["x" "y"] :ns "interface"}]
            :implementing-components ["invoice" "invoice2"]
            :implementing-deps ["user"]}
           {:name "payment",
            :definitions [{:name "pay", :type "function", :parameters ["a"] :ns "interface"}
                          {:name "pay", :type "function", :parameters ["b"] :ns "interface"}]
            :implementing-components ["payment"],
            :implementing-deps ["invoice"]}
           {:name "user",
            :definitions [{:name "func1", :type "function", :parameters [] :ns "interface"}
                          {:name "func2", :type "function", :parameters ["a" "b"] :ns "interface"}
                          {:name "func2", :type "function", :parameters ["x" "y"] :ns "interface"}
                          {:name "func3", :type "function", :parameters ["a" "b" "c"] :ns "interface"}
                          {:name "func3", :type "function", :parameters ["x" "y" "z"] :ns "interface"}]
            :implementing-components ["user1" "user2"]
            :implementing-deps ["payment" "auth"]}]
         (deps/interface-deps interfaces components))))
