(ns polylith.clj.core.workspace.interfc
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.text-table.info-tables :as info-tables])
  (:gen-class))

(defn enrich-workspace [workspace user-input]
  (core/enrich-workspace workspace user-input))

(defn print-info [workspace]
  (info-tables/print-info workspace))
