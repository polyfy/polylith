(ns polylith.clj.core.test-runner.interfc
  (:require [polylith.clj.core.test-runner.core :as core]))

(defn run [workspace env run-all? run-env-tests?]
  (core/run workspace env run-all? run-env-tests?))
