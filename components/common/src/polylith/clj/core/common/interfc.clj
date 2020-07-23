(ns polylith.clj.core.common.interfc
  (:require [polylith.clj.core.common.core :as core]
            [polylith.clj.core.common.class-loader :as class-loader]
            [polylith.clj.core.common.message :as msg]))

(defn ns-to-path [namespace]
  (core/ns-to-path namespace))

(defn path-to-ns [namespace]
  (core/path-to-ns namespace))

(defn pretty-messages
  ([workspace]
   (msg/pretty-messages workspace))
  ([messages color-mode]
   (msg/pretty-messages messages color-mode)))

(defn messages-without-colors [workspace]
  (msg/messages-without-colors workspace))

(defn top-namespace [namespace]
  (core/top-namespace namespace))

(defn filter-clojure-paths [paths]
  (core/filter-clojure-paths paths))

(defn create-class-loader [paths color-mode]
  (class-loader/create-class-loader paths color-mode))

(defn eval-in [class-loader form]
  (class-loader/eval-in class-loader form))

(defn interface? [name]
  (contains? #{"interface" "interfc"} name))

(defn find-brick [workspace brick-name]
  (core/find-brick workspace brick-name))

(defn find-environment [environment-name environments]
  (core/find-environment environment-name environments))
