(ns polylith.clj.core.check.interface
  (:require [polylith.clj.core.check.core :as core]))

(defn check [workspace]
  (core/check workspace))

(defn print-check [workspace color-mode]
  (core/print-check workspace color-mode))
