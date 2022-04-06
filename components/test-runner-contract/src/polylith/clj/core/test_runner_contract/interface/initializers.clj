(ns polylith.clj.core.test-runner-contract.interface.initializers)

(defn ->constructor-var [create-test-runner-sym]
  (when-not (contains? #{nil :default} create-test-runner-sym)
    (requiring-resolve create-test-runner-sym)))
