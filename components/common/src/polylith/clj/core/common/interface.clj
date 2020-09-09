(ns polylith.clj.core.common.interface
  (:require [polylith.clj.core.common.core :as core]
            [polylith.clj.core.common.class-loader :as class-loader]
            [polylith.clj.core.common.validate-args :as validate-args]))

(def poly-git-sha "69e70df8882f4d9a701ab99681a4a6870bdf052b")

(defn ns-to-path [namespace]
  (core/ns-to-path namespace))

(defn path-to-ns [namespace]
  (when namespace
    (core/path-to-ns namespace)))

(defn suffix-ns-with-dot [namespace]
  (core/sufix-ns-with-dot namespace))

(defn filter-clojure-paths [paths]
  (core/filter-clojure-paths paths))

(defn create-class-loader [paths color-mode]
  (class-loader/create-class-loader paths color-mode))

(defn eval-in [class-loader form]
  (class-loader/eval-in class-loader form))

(defn find-brick [name workspace]
  (core/find-brick name workspace))

(defn find-component [name components]
  (core/find-component name components))

(defn find-base [base-name bases]
  (core/find-base base-name bases))

(defn find-environment [environment-name environments]
  (core/find-environment environment-name environments))

(defn validate-args [unnamed-args example]
  (validate-args/validate unnamed-args example))

(defn color-mode [user-input]
  (core/color-mode user-input))

(defn workspace-dir [user-input]
  (core/workspace-dir user-input))
