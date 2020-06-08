(ns polylith.shared.interface
  (:require [polylith.shared.core :as core]))

(defn top-namespace [namespace]
  (core/top-namespace namespace))
