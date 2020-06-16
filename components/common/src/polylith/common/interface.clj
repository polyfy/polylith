(ns polylith.common.interface
  (:require [polylith.common.core :as core]))

(defn make-classpath [libraries source-paths]
  (core/make-classpath libraries source-paths))

(defn run-in-jvm [classpath expression dir ex-msg]
  (core/run-in-jvm classpath expression dir ex-msg))

(defn throw-polylith-exception [message]
  (core/throw-polylith-exception message))

(defn top-namespace [namespace]
  (core/top-namespace namespace))
