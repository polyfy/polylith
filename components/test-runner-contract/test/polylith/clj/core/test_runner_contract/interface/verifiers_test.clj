(ns polylith.clj.core.test-runner-contract.interface.verifiers-test
  (:require
   [clojure.test :refer :all]
   [polylith.clj.core.test-runner-contract.interface :as test-runner-contract]
   [polylith.clj.core.test-runner-contract.interface.initializers :as test-runner-initializers]
   [polylith.clj.core.test-runner-contract.interface.verifiers :as sut])
  (:import
   (polylith.clj.core.test_runner_contract.interface TestRunner)))

(defn one-arity-ctor [_])
(defn good-variable-arity-ctor-1 [& _])
(defn good-variable-arity-ctor-2 [_ & _])

(defn good-multiple-arity-ctor
  ([]) ([_ _]) ([_]))

(defn no-suitable-arity-ctor
  ([]) ([_ _]) ([_ _ & _]))

(deftest constructor-var-validity
  (are [pred sym]
       (-> sym test-runner-initializers/->constructor-var sut/valid-constructor-var? pred)
    true? `one-arity-ctor
    true? `good-variable-arity-ctor-1
    true? `good-variable-arity-ctor-2
    true? `good-multiple-arity-ctor
    false? `no-suitable-arity-ctor))

(deftest ensure-valid-test-runner-throws-when-invalid
  (is (thrown? Exception (sut/ensure-valid-test-runner {}))))

(deftest ensure-valid-test-runner-returns-runner-when-valid
  (is (instance?
       TestRunner
       (sut/ensure-valid-test-runner
        (reify test-runner-contract/TestRunner)))))
