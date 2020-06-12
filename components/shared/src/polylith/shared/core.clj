(ns polylith.shared.core
  (:require [clojure.string :as str])
  (:import (java.util.concurrent ExecutionException)))

(defn throw-polylith-exception [message]
  (throw (ExecutionException. message (Exception.))))

(defn top-namespace [top-namespace]
  "Makes sure the top namespace ends with a dot (.) - if not empty."
  (if (str/blank? top-namespace)
    ""
    (if (str/ends-with? top-namespace ".")
      top-namespace
      (str top-namespace "."))))
