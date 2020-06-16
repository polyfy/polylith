(ns polylith.common.core
  (:require [clojure.java.shell :as shell]
            [clojure.tools.deps.alpha :as tools-deps]
            [clojure.string :as str])
  (:import (java.util.concurrent ExecutionException)))

(defn throw-polylith-exception [message]
  (throw (ExecutionException. message (Exception.))))

(defn make-classpath [libraries source-paths]
  (tools-deps/make-classpath libraries source-paths nil))

(defn run-in-jvm [classpath expression dir ex-msg]
  (let [{:keys [exit err out]} (shell/sh "java" "-cp" classpath "clojure.main" "-e" expression :dir dir)]
    (if (= 0 exit)
      out
      (throw (ex-info ex-msg {:err err :exit-code exit})))))

(defn top-namespace [top-namespace]
  "Makes sure the top namespace ends with a dot (.) - if not empty."
  (if (str/blank? top-namespace)
    ""
    (if (str/ends-with? top-namespace ".")
      top-namespace
      (str top-namespace "."))))
