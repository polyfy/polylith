(ns polylith.clj.core.test-runner.interface.runner-init
  (:require [polylith.clj.core.test-runner.runner-init :as core]))

(defn ->constructor-var [make-test-runner-sym]
  (core/->constructor-var make-test-runner-sym))

(defn valid-constructor-var? [candidate]
  (core/valid-constructor-var? candidate))

(defn ensure-valid-test-runner [candidate]
  (core/ensure-valid-test-runner candidate))
