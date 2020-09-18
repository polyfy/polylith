(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core]))

(defn environments-to-deploy []
  (core/environments-to-deploy))

(defn workspace [keys]
  (core/workspace keys))
