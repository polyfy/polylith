(ns polylith.workspace.interfaces-defined-test
  (:require [polylith.workspace.interfaces-defined :as idefined]))

(def components '[{:type "component",
                   :name "user1",
                   :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                   :interface {:name "user", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}
                  {:type "component",
                   :name "user2",
                   :imports [{:ns-path "user/interface.clj", :imports [user.core]} {:ns-path "user/core.clj", :imports []}],
                   :interface {:name "user", :declarations [{:type function, :name add-two, :overloads [{:args [x], :arity 1}]}]}}])

(idefined/errors ["user"] components)
