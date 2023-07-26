(ns ^:no-doc polylith.clj.core.test-runner-contract.interface.initializers
  (:require [polylith.clj.core.test-runner-contract.initializers :as core]))

(defn ->constructor-var [create-test-runner-sym]
  (core/->constructor-var create-test-runner-sym))
