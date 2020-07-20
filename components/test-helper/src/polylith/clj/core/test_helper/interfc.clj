(ns polylith.clj.core.test-helper.interfc
  (:require [polylith.clj.core.test-helper.core :as core]))

(defn root-dir []
  @core/root-dir)

(defn test-setup-and-tear-down [function]
  (core/test-setup-and-tear-down function))

(defn execute-command [cmd & [arg1 arg2 arg3]]
  (core/execute-command cmd arg1 arg2 arg3))

(defn paths [dir]
  (core/paths dir))

(defn content [ws-dir directory]
  (core/content ws-dir directory))
