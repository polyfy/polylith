(ns polylith.clj.core.test-runner-contract.interface.verifiers
  (:require [polylith.clj.core.test-runner-contract.verifiers :as core]))

(defn valid-constructor-var? [candidate]
  (core/valid-constructor-var? candidate))

(defn valid-test-runner? [candidate]
  (core/valid-test-runner? candidate))

(defn valid-external-test-runner? [candidate]
  (core/valid-external-test-runner? candidate))

(defn ensure-valid-test-runner [candidate]
  (core/ensure-valid-test-runner candidate))
