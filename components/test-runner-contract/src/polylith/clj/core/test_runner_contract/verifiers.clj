(ns polylith.clj.core.test-runner-contract.verifiers
  (:require [polylith.clj.core.test-runner-contract.interface :as test-runner-contract]
            [polylith.clj.core.util.interface :as util]))

(let [ampersand-symbol? #{'&}
      fully-variadic? #(ampersand-symbol? (first %))
      not-ampersand (complement ampersand-symbol?)
      has-one-fixed-arg #(= 1 (count (take-while not-ampersand %)))
      can-be-invoked-with-one-arg-xf (map (some-fn fully-variadic? has-one-fixed-arg))]

  (defn valid-constructor-var? [candidate]
    (-> (fn? (deref candidate))
        (and (->> candidate (meta) (:arglists) (util/xf-some can-be-invoked-with-one-arg-xf)))
        (boolean))))

(defn valid-test-runner? [candidate]
  (satisfies? test-runner-contract/TestRunner candidate))

(defn ensure-valid-test-runner [candidate]
  (when-not (valid-test-runner? candidate)
    (throw (ex-info "Test runners must satisfy the TestRunner protocol" {:candidate candidate})))
  candidate)
