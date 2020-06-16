(ns polylith.util.interface
  (:require [polylith.util.core :as core]))

(defn ordered-map [& keyvals]
  (core/ordered-map keyvals))
