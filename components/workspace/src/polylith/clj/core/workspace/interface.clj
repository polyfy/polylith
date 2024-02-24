(ns ^:no-doc polylith.clj.core.workspace.interface
  (:require [polylith.clj.core.workspace.core :as core])
  (:gen-class))

(defn workspace [user-input]
 (core/workspace user-input))
