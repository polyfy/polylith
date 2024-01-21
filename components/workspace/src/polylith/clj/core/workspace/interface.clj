(ns ^:no-doc polylith.clj.core.workspace.interface
  (:require [polylith.clj.core.workspace.core :as core])
  (:gen-class))

(defn enrich-workspace [workspace]
  (core/enrich-workspace workspace))
