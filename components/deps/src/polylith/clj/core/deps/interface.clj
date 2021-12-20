(ns polylith.clj.core.deps.interface
  (:require [polylith.clj.core.deps.interface-deps :as ifc-deps]
            [polylith.clj.core.deps.lib-deps :as lib-deps]
            [polylith.clj.core.deps.project-brick-deps :as project-brick-deps]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.project-brick-deps-table :as project-brick-deps-table]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as workspace-deps-table]
            [polylith.clj.core.deps.text-table.project-deps-table :as project-deps-table]))

(defn interface-ns-deps [suffixed-top-ns interface-name interface-names brick-namespaces]
  (ifc-deps/interface-ns-deps suffixed-top-ns interface-name interface-names brick-namespaces))

(defn interface-deps [suffixed-top-ns interface-names brick]
  (ifc-deps/interface-deps suffixed-top-ns interface-names brick))

(defn project-deps [components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns bricks-to-test]
  (project-brick-deps/project-deps components bases component-names-src component-names-test base-names-src base-names-test suffixed-top-ns bricks-to-test))

(defn print-project-brick-table [workspace project-name brick-name]
  (project-brick-deps-table/print-table workspace project-name brick-name))

(defn print-brick-table [workspace brick-name]
  (brick-deps-table/print-table workspace brick-name))

(defn print-workspace-table [workspace]
  (workspace-deps-table/print-table workspace))

(defn print-project-table [workspace project-name is-all]
  (project-deps-table/print-table workspace project-name is-all))

(defn resolve-deps [project settings is-verbose]
  (lib-deps/resolve-deps project settings is-verbose))
