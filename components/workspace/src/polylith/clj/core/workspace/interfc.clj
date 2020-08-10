(ns polylith.clj.core.workspace.interfc
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.user-config.interfc :as user-config]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table])
  (:gen-class))

(defn enrich-workspace [workspace test-settings]
  (core/enrich-workspace workspace test-settings))

(defn print-table [workspace show-loc?]
  (let [thousand-sep (user-config/thousand-separator)]
    (ws-table/print-table workspace thousand-sep show-loc?)))

(defn enrich-workspace-str-keys [workspace test-settings]
  "Used by the polylith.core API"
  (core/enrich-workspace-str-keys workspace test-settings))

(defn print-table-str-keys [workspace show-loc?]
  "Used by the polylith.core API"
  (let [thousand-sep (user-config/thousand-separator)]
    (ws-table/print-table-str-keys workspace thousand-sep show-loc?)))
