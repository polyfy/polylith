(ns polylith.common.core
  (:require [clojure.java.shell :as shell]
            [clojure.tools.deps.alpha :as tools-deps]))

(defn make-classpath [libraries source-paths]
  (tools-deps/make-classpath libraries source-paths nil))

(defn run-in-jvm [classpath expression dir ex-msg]
  (let [{:keys [exit err out]} (shell/sh "java" "-cp" classpath "clojure.main" "-e" expression :dir dir)]
    (if (= 0 exit)
      out
      (throw (ex-info ex-msg {:err err :exit-code exit})))))
