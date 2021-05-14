(ns polylith.clj.core.test-helper.interface
  (:require [polylith.clj.core.test-helper.core :as core]
            [polylith.clj.core.user-input.interface :as user-input]))

(defn root-dir []
  @core/root-dir)

(defn content [dir filename]
  (core/content dir filename))

(defn execute-command [current-dir & args]
  (core/execute-command current-dir args))

(defn paths [dir]
  (core/paths dir))

(defn test-setup-and-tear-down [function]
  (core/test-setup-and-tear-down function))

(defn user-home []
  core/user-home)
