(ns polylith.clj.core.test-helper.interfc
  (:require [polylith.clj.core.test-helper.core :as core]))

(defn test-setup-and-tear-down [function]
  (core/test-setup-and-tear-down function))

(defn execute-command [current-dir cmd & [arg1 arg2]]
  (core/execute-command current-dir cmd arg1 arg2))

(defn paths [dir]
  (core/paths dir))

(defn content [dir filename]
  (core/content dir filename))
