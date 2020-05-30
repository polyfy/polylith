(ns polylith.workspace-clj.interface
  (:require [polylith.workspace-clj.core :as core]))

(defn read-workspace-from-disk
  ([ws-path]
   (core/read-workspace-from-disk ws-path))
  ([ws-path config]
   (core/read-workspace-from-disk ws-path config)))
