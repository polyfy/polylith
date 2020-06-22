(ns polylith.common.interface
  (:require [polylith.common.core :as core]
            [polylith.common.class-loader :as class-loader]))

(defn top-namespace [namespace]
  (core/top-namespace namespace))

(defn filter-clojure-paths [paths]
  (core/filter-clojure-paths paths))

(defn create-class-loader [paths]
  (class-loader/create-class-loader paths))

(defn eval-in [class-loader form]
  (class-loader/eval-in class-loader form))
