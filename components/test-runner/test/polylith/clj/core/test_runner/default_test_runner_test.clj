(ns polylith.clj.core.test-runner.default-test-runner-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.test-runner-plugin-init.interface :as runner-init]
            [polylith.clj.core.test-runner.default-test-runner :as sut]))

(deftest default-test-runner-is-valid
  (let [ctor (runner-init/->constructor-var `sut/make)]
    (is (runner-init/valid-constructor-var? ctor))
    (is (runner-init/ensure-valid-test-runner ((deref ctor) {})))))
