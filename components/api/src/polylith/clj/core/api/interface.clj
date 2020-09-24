(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core])
  (:gen-class))

(defn environments-to-deploy []
  (core/environments-to-deploy))

(defn workspace [stable-point & keys]
  (core/workspace stable-point keys))
