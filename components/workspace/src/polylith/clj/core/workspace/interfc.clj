(ns polylith.clj.core.workspace.interfc
  (:require [polylith.clj.core.workspace.core :as core]
            [polylith.clj.core.workspace.source :as source]
            [polylith.clj.core.workspace.dependencies :as deps]
            [polylith.clj.core.workspace.text-table :as text-table])
  (:gen-class))

(defn enrich-workspace [workspace]
  (core/enrich-workspace workspace))

(defn enrich-workspace-str-keys [workspace]
  "Used by the polylith.core API"
  (core/enrich-workspace-str-keys workspace))

(defn print-table [workspace]
  (text-table/print-table workspace))

(defn print-table-str-keys [workspace]
  "Used by the polylith.core API"
  (text-table/print-table-str-keys workspace))

(defn src-paths
  ([workspace env-group]
   (source/paths workspace env-group false))
  ([workspace env-group include-tests?]
   (source/paths workspace env-group include-tests?)))

(defn test-namespaces [workspace env-group]
  (source/test-namespaces workspace env-group))
