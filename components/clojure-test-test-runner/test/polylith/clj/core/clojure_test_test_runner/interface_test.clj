(ns polylith.clj.core.clojure-test-test-runner.interface-test
  (:require
   [clojure.test :refer :all]
   [polylith.clj.core.clojure-test-test-runner.interface :as sut]
   [polylith.clj.core.test-runner-contract.interface.initializers :as test-runner-initializers]
   [polylith.clj.core.test-runner-contract.interface.verifiers :as test-runner-verifiers]))

(deftest clojure-test-test-runner-is-valid
  (let [constructor (test-runner-initializers/->constructor-var `sut/create)]
    (is (test-runner-verifiers/valid-constructor-var? constructor))
    (let [test-runner (constructor {})]
      (is (test-runner-verifiers/valid-test-runner? test-runner))
      (is (test-runner-verifiers/ensure-valid-test-runner test-runner)))))
