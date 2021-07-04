(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core])
  (:gen-class))

(defn projects-to-deploy [since]
  (core/projects-to-deploy since))

(defn workspace [stable-point & keys]
  (core/workspace stable-point keys))
