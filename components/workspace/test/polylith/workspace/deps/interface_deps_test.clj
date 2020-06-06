(ns polylith.workspace.deps.interface-deps-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.deps.interface-deps :as interface-deps]))

(def interfaces '[{:name "auth", :declarations [{:name add-two, :type function, :signature [x]}], :implemented-by ["auth"]}
                  {:name "invoice"
                   :declarations [{:type data, :name abc}
                                  {:name func1, :type function, :signature [a]}
                                  {:name func1, :type function, :signature [b]}
                                  {:name func1, :type function, :signature [a b]}
                                  {:name func1, :type function, :signature [x y]}]
                   :implemented-by ["invoice" "invoice2"]}
                  {:name "payment"
                   :declarations [{:name pay, :type function, :signature [a]} {:name pay, :type function, :signature [b]}],
                   :implemented-by ["payment"]}
                  {:name "user"
                   :declarations [{:name func1, :type function, :signature []}
                                  {:name func2, :type function, :signature [a b]}
                                  {:name func2, :type function, :signature [x y]}
                                  {:name func3, :type function, :signature [a b c]}
                                  {:name func3, :type function, :signature [x y z]}]
                   :implemented-by ["user1" "user2"]}])

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

(deftest with-deps--a-workspace-with-dependencies-from-different-components--should-be-merged-into-correct-interfaces
  (is (= '[{:name "auth",
            :declarations [{:name add-two, :type function, :signature [x]}],
            :implemented-by ["auth"],
            :dependencies []}
           {:name "invoice",
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
            :declarations [{:name func1, :type function, :signature []}
                           {:name func2, :type function, :signature [a b]}
                           {:name func2, :type function, :signature [x y]}
                           {:name func3, :type function, :signature [a b c]}
                           {:name func3, :type function, :signature [x y z]}],
            :implemented-by ["user1" "user2"],
            :dependencies ["payment" "auth"]}]
         (interface-deps/with-deps interfaces components))))
