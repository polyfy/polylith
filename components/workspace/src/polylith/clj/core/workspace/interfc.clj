(ns polylith.clj.core.workspace.interfc
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.text-table.info-tables :as info-tables])
  (:gen-class))

(defn enrich-workspace [workspace user-input]
  (core/enrich-workspace workspace user-input))

(defn print-info [workspace show-loc?]
  (info-tables/print-info workspace show-loc?))

(defn enrich-workspace-str-keys [workspace user-input]
  "Used by the polylith.core API"
  (core/enrich-workspace-str-keys workspace user-input))

(defn print-table-str-keys [workspace show-loc?]
  "Used by the polylith.core API"
  (info-tables/print-info workspace show-loc?))
