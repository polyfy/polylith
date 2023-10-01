(ns ^:no-doc polylith.clj.core.workspace.interface
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.text-table.info-table :as info-table])
  (:gen-class))

(defn enrich-workspace [workspace]
  (core/enrich-workspace workspace))

(defn print-info [workspace]
  (info-table/print-info workspace))

(defn table [workspace]
  (info-table/table workspace))
