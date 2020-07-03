(ns polylith.core.common.interface
  (:require [polylith.core.common.core :as core]
            [polylith.core.common.class-loader :as class-loader]))

(defn top-namespace [namespace]
  (core/top-namespace namespace))

(defn filter-clojure-paths [paths]
  (core/filter-clojure-paths paths))

(defn create-class-loader [paths color-mode]
  (class-loader/create-class-loader paths color-mode))

(defn eval-in [class-loader form]
  (class-loader/eval-in class-loader form))
