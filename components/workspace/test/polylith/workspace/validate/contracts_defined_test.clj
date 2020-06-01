(ns polylith.workspace.validate.contracts-defined-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.validate.contracts-defined :as cdefined]))

(deftest one-contract-defined--returns-no-error-message
  (let [components '[{:type "component",
                      :name "user1",
                      :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                      :interface {:name "user", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}
                     {:type "component",
                      :name "user",
                      :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                      :interface {:name "user", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}]]

    (is (= []
           (cdefined/errors components)))))

(deftest no-contract-defined--return-error-messages
  (let [components '[{:type "component",
                      :name "user1",
                      :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                      :interface {:name "user", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}
                     {:type "component",
                      :name "user2",
                      :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                      :interface {:name "user", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}
                     {:type "component",
                      :name "invoice1",
                      :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                      :interface {:name "invoice", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}
                     {:type "component",
                      :name "invoice2",
                      :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                      :interface {:name "invoice", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}]]
    (is (= [(str "No contract was found for the 'invoice' interface. "
                 "Make sure you have a component named 'invoice' or specify a component "
                 "as contract holder in the :polylith config and select one of: invoice1, invoice2")
            (str "No contract was found for the 'user' interface. "
                 "Make sure you have a component named 'user' or specify a component "
                 "as contract holder in the :polylith config and select one of: user1, user2")]
           (cdefined/errors components)))))
