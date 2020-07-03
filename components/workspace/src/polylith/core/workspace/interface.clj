(ns polylith.core.workspace.interface
  (:require [polylith.core.workspace.core :as core]
            [polylith.core.workspace.source :as source]
            [polylith.core.workspace.dependencies :as deps]
            [polylith.core.workspace.text-table :as text-table]))

(defn enrich-workspace [workspace]
  (core/enrich-workspace workspace))

(defn print-table [workspace]
  (text-table/print-table workspace))

(defn src-paths
  ([workspace env-group]
   (source/paths workspace env-group false))
  ([workspace env-group include-tests?]
   (source/paths workspace env-group include-tests?)))

(defn lib-paths
  ([workspace]
   (deps/paths workspace nil false nil))
  ([workspace env-group]
   (deps/paths workspace env-group false nil))
  ([workspace env-group include-tests?]
   (deps/paths workspace env-group include-tests? nil))
  ([workspace env-group include-tests? additional-deps]
   (deps/paths workspace env-group include-tests? additional-deps)))

(defn test-namespaces [workspace env-group]
  (source/test-namespaces workspace env-group))
