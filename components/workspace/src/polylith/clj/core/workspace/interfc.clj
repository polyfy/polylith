(ns polylith.clj.core.workspace.interfc
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.text-table.info-tables :as info-tables])
  (:gen-class))

(defn enrich-workspace [workspace]
  (core/enrich-workspace workspace))

(defn print-info [workspace]
  (info-tables/print-info workspace))
