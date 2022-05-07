(ns polylith.clj.core.test-runner-contract.interface.initializers-test
  (:require
   [clojure.test :refer :all]
   [polylith.clj.core.test-runner-contract.interface.initializers :as sut]))

(deftest ->constructor-var-throws-when-unable-to-load
  (is (thrown? Exception (sut/->constructor-var 'missing.ns/sym)))
  (is (thrown? Exception (sut/->constructor-var `existing-ns-missing-sym))))

(deftest ->constructor-var-returns-var
  (is (var? (sut/->constructor-var 'clojure.core/map))))
