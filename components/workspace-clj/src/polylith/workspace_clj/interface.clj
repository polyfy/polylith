(ns polylith.workspace-clj.interface
  (:require [polylith.workspace-clj.core :as core]))

(defn workspace-from-disk
  ([ws-path]
   (core/workspace-from-disk ws-path))
  ([ws-path config]
   (core/workspace-from-disk ws-path config)))
