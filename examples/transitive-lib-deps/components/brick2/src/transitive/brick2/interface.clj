(ns transitive.brick2.interface
  (:require [com.stuartsierra.dependency :as dependency]))

(defn dependency-graph []
  (dependency/graph))
