(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core])
  (:gen-class))

(defn projects-to-deploy
  "Returns the projects that have been affected since last deploy,
   tagged in git following the pattern defined by :release-tag-pattern in
   deps.edn, or v[0-9]* if not defined."
  [since]
  (core/projects-to-deploy since))

(defn workspace [stable-point & keys]
  (core/workspace stable-point keys))
