(ns ^:no-doc polylith.clj.core.test-runner-contract.initializers)

(defn ensure-var [candidate sym]
  (when-not (var? candidate)
    (throw
     (ex-info (str "Unable to resolve symbol " sym " to a var.") {:symbol sym}))))

(defn ->constructor-var [create-test-runner-sym]
  (doto (requiring-resolve create-test-runner-sym)
    (ensure-var create-test-runner-sym)))
