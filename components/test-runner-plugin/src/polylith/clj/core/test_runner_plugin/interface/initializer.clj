(ns polylith.clj.core.test-runner-plugin.interface.initializer
  (:require [polylith.clj.core.test-runner-plugin.initializer :as core]))

(defn ->constructor-var [make-test-runner-sym]
  (core/->constructor-var make-test-runner-sym))

(defn valid-constructor-var? [candidate]
  (core/valid-constructor-var? candidate))

(defn ensure-valid-test-runner [candidate]
  (core/ensure-valid-test-runner candidate))
