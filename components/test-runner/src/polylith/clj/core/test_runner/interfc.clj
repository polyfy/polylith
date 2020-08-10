(ns polylith.clj.core.test-runner.interfc
  (:require [polylith.clj.core.test-runner.core :as core]))

(defn run [workspace env]
  (core/run workspace env))
