(ns polylith.workspace-clj.interface
  (:require [polylith.workspace-clj.core :as core]))

(defn read-workspace-from-disk [ws-path config]
  (core/read-workspace-from-disk ws-path config))
