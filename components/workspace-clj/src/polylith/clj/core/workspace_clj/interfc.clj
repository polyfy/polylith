(ns polylith.clj.core.workspace-clj.interfc
  (:require [polylith.clj.core.workspace-clj.core :as core]))

(defn workspace-from-disk
  ([ws-dir]
   (core/workspace-from-disk ws-dir))
  ([ws-dir config]
   (core/workspace-from-disk ws-dir config)))
