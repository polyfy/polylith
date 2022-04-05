(ns polylith.clj.core.test-runner.default-test-runner-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-runner-plugin.interface.initializer :as test-runner-initializer]
            [polylith.clj.core.test-runner.default-test-runner :as sut]))

(deftest default-test-runner-is-valid
  (let [ctor (test-runner-initializer/->constructor-var `sut/make)]
    (is (test-runner-initializer/valid-constructor-var? ctor))
    (is (test-runner-initializer/ensure-valid-test-runner ((deref ctor) {})))))
