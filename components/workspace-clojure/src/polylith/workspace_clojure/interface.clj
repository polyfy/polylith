(ns polylith.workspace-clojure.interface
  (:require [polylith.workspace-clojure.core :as core]))

(defn workspace-from-disk
  ([ws-path]
   (core/workspace-from-disk ws-path))
  ([ws-path config]
   (core/workspace-from-disk ws-path config)))
