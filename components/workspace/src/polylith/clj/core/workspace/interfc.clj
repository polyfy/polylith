(ns polylith.clj.core.workspace.interfc
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.text-table.info-tables :as info-tables]
            [polylith.clj.core.workspace.text-table.lib-table :as lib-table]
            [polylith.clj.core.workspace.text-table.lib-version-table :as lib-version-table])
  (:gen-class))

(defn enrich-workspace [workspace user-input]
  (core/enrich-workspace workspace user-input))

(defn print-info [workspace]
  (info-tables/print-info workspace))

(defn print-lib-tables [workspace]
  (lib-table/print-table workspace)
  (println)
  (lib-version-table/print-table workspace))
