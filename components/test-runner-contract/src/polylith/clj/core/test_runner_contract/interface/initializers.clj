(ns polylith.clj.core.test-runner-contract.interface.initializers)

(defn ->constructor-var [create-test-runner-sym]
  (when-not (contains? #{nil :default} create-test-runner-sym)
    (doto (requiring-resolve create-test-runner-sym)
      (-> (var?)
          (when-not
            (throw
             (ex-info (str "Unable to resolve symbol "
                           create-test-runner-sym
                           " to a var.")
                      {:symbol create-test-runner-sym})))))))
