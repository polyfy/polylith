(ns polylith.shared.interface
  (:require [polylith.shared.core :as core]))

(defn throw-polylith-exception [message]
  (core/throw-polylith-exception message))

(defn top-namespace [namespace]
  (core/top-namespace namespace))
