(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core])
  (:gen-class))

(defn projects-to-deploy []
  (core/projets-to-deploy))

(defn workspace [stable-point & keys]
  (core/workspace stable-point keys))
