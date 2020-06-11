(ns polylith.deps.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.deps.interface :as deps]))

(def interfaces '[{:name "auth",
                   :definitions [{:name "add-two", :type "function", :parameters ["x"]}], :implementing-components ["auth"]}
                  {:name "invoice"
                   :definitions [{:name "abc" :type "data" :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["b"] :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["a" "b"] :sub-ns ""}
                                 {:name "func1", :type "function", :parameters ["x" "y"] :sub-ns ""}]
                   :implementing-components ["invoice" "invoice2"]}
                  {:name "payment"
                   :definitions [{:name "pay", :type "function", :parameters ["a"] :sub-ns ""}
                                 {:name "pay", :type "function", :parameters ["b"] :sub-ns ""}],
                   :implementing-components ["payment"]}
                  {:name "user"
                   :definitions [{:name "func1", :type "function", :parameters [] :sub-ns ""}
                                 {:name "func2", :type "function", :parameters ["a" "b"] :sub-ns ""}
                                 {:name "func2", :type "function", :parameters ["x" "y"] :sub-ns ""}
                                 {:name "func3", :type "function", :parameters ["a" "b" "c"] :sub-ns ""}
                                 {:name "func3", :type "function", :parameters ["x" "y" "z"] :sub-ns ""}]
                   :implementing-components ["user1" "user2"]}])

(def components '[{:name "auth"
                   :interface {:name "auth",
                               :definitions [{:name "add-two", :type "function", :parameters ["x"] :sub-ns ""}]}
                   :dependencies []}
                  {:name "invoice"
                   :interface {:name "invoice"
                               :definitions [{:name "abc" :type "data" :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["a" "b"] :sub-ns ""}]}
                   :dependencies ["user"]}
                  {:name "invoice2"
                   :interface {:name "invoice"
                               :definitions [{:name "func1", :type "function", :parameters ["b"] :sub-ns ""}
                                             {:name "func1", :type "function", :parameters ["x" "y"] :sub-ns ""}]}
                   :dependencies []}
                  {:name "payment"
                   :interface {:name "payment"
                               :definitions [{:name "pay", :type "function", :parameters ["a"] :sub-ns ""}
                                             {:name "pay", :type "function", :parameters ["b"] :sub-ns ""}]}
                   :dependencies ["invoice"]}
                  {:name "user1"
                   :interface {:name "user"
                               :definitions [{:name "func1", :type "function", :parameters [] :sub-ns ""}
                                             {:name "func2", :type "function", :parameters ["a" "b"] :sub-ns ""}
                                             {:name "func3", :type "function", :parameters ["a" "b" "c"] :sub-ns ""}]}
                   :dependencies ["payment"]}
                  {:name "user2"
                   :interface {:name "user"
                               :definitions [{:name "func2", :type "function", :parameters ["x" "y"] :sub-ns ""}
                                             {:name "func3", :type "function", :parameters ["x" "y" "z"] :sub-ns ""}]}
                   :dependencies ["auth"]}])

(deftest interface-deps--a-workspace-with-implementing-deps-from-different-components--should-be-merged-into-a-single-list-of-dependencies
  (is (= '[{:name "auth"
            :definitions [{:name "add-two", :type "function", :parameters ["x"]}]
            :implementing-components ["auth"]
            :implementing-deps []}
           {:name "invoice"
            :definitions [{:name "abc" :type "data" :sub-ns ""}
                          {:name "func1", :type "function", :parameters ["a"] :sub-ns ""}
                          {:name "func1", :type "function", :parameters ["b"] :sub-ns ""}
                          {:name "func1", :type "function", :parameters ["a" "b"] :sub-ns ""}
                          {:name "func1", :type "function", :parameters ["x" "y"] :sub-ns ""}]
            :implementing-components ["invoice" "invoice2"]
            :implementing-deps ["user"]}
           {:name "payment",
            :definitions [{:name "pay", :type "function", :parameters ["a"] :sub-ns ""}
                          {:name "pay", :type "function", :parameters ["b"] :sub-ns ""}]
            :implementing-components ["payment"],
            :implementing-deps ["invoice"]}
           {:name "user",
            :definitions [{:name "func1", :type "function", :parameters [] :sub-ns ""}
                          {:name "func2", :type "function", :parameters ["a" "b"] :sub-ns ""}
                          {:name "func2", :type "function", :parameters ["x" "y"] :sub-ns ""}
                          {:name "func3", :type "function", :parameters ["a" "b" "c"] :sub-ns ""}
                          {:name "func3", :type "function", :parameters ["x" "y" "z"] :sub-ns ""}]
            :implementing-components ["user1" "user2"]
            :implementing-deps ["payment" "auth"]}]
         (deps/interface-deps interfaces components))))
