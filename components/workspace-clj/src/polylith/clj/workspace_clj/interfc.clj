(ns polylith.clj.workspace-clj.interfc
  (:require [polylith.clj.workspace-clj.core :as core]))

(defn workspace-from-disk
  ([ws-path]
   (core/workspace-from-disk ws-path))
  ([ws-path config]
   (core/workspace-from-disk ws-path config)))
