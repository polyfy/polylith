(ns polylith.clj.core.api.interface
  (:require [polylith.clj.core.api.core :as core])
  (:gen-class))

(defn projects-to-deploy
  "Returns the projects that have been affected since last deploy,
   tagged in git following the pattern defined by :release-tag-pattern in
   deps.edn, or v[0-9]* if not defined."
  [since]
  (core/projects-to-deploy since))

(defn workspace
  "Returns the workspace or part of the workspace by sending in either
   a key that can be found in the :tag-patterns keys in workspace.edn,
   optionally prefixed with 'previous-', or a git SHA, as the first argument,
   and a list of keywords, strings, or numbers as the second argument.
   :keys and :count are also valid keys to send in. If keys are empty,
   returns the whole workspace."
  [stable-point & keys]
  (core/workspace stable-point keys))
