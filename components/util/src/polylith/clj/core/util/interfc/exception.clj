(ns polylith.clj.core.util.interfc.exception
  (:require [clojure.stacktrace :as stacktrace]))

(defn print-error-message [e]
  (when-let [message (.getMessage e)]
    (println message)))

(defn print-exception [e]
  (println (or (-> e ex-data :err) (.getMessage e))))

(defn print-stacktrace [e]
  (stacktrace/print-stack-trace e))
