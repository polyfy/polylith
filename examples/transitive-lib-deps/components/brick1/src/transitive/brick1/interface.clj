(ns transitive.brick1.interface
  (:require [com.stuartsierra.component :as component]
            [com.stuartsierra.dependency :as dependency]))

(defn new-system []
  ;; Uses 'component' directly (a declared dependency) ...
  (component/system-map))

(defn dependency-graph []
  ;; ... and 'dependency' indirectly. 'com.stuartsierra/dependency' is not declared
  ;; in brick1/deps.edn; it is only on the classpath because 'component' depends on it.
  (dependency/graph))
