(ns polylith.clj.core.workspace.interface
  (:require [polylith.clj.core.util.interface.time :as time-util]
            [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.text-table.info-table :as info-table])
  (:gen-class))

(defn enrich-workspace [workspace]
  (time-util/tap-seconds "#enrich-workspace" (core/enrich-workspace workspace)))

(defn print-info [workspace]
  (info-table/print-info workspace))

(defn table [workspace]
  (info-table/table workspace))
