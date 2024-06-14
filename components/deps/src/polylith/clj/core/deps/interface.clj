(ns ^:no-doc polylith.clj.core.deps.interface
  (:require [polylith.clj.core.deps.lib-deps :as lib-deps]
            [polylith.clj.core.deps.project-brick-deps.project-deps :as project-brick-deps]
            [polylith.clj.core.deps.brick-deps.core :as brick-deps]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.brick-project-deps-table :as brick-project-deps-table]
            [polylith.clj.core.deps.text-table.workspace-deps-table :as workspace-deps-table]
            [polylith.clj.core.deps.text-table.workspace-project-deps-table :as workspace-project-deps-table]))

(defn convert-dep-to-symbol [dep]
  (lib-deps/convert-dep-to-symbol dep))

(defn brick-deps [suffixed-top-ns interface-names base-names interface-ns brick]
  (brick-deps/brick-deps suffixed-top-ns interface-names base-names interface-ns brick))

(defn project-deps [project-name
                    components bases workspaces
                    component-names-src component-names-test
                    base-names-src base-names-test
                    component-names-src-x component-names-test-x
                    base-names-src-x base-names-test-x
                    suffixed-top-ns brick-names-to-test]
  (project-brick-deps/project-deps project-name
                                   components bases workspaces
                                   component-names-src component-names-test
                                   base-names-src base-names-test
                                   component-names-src-x component-names-test-x
                                   base-names-src-x base-names-test-x
                                   suffixed-top-ns brick-names-to-test))

(defn print-brick-project-table [workspace project-name brick-name]
  (brick-project-deps-table/print-table workspace project-name brick-name))

(defn print-brick-table [workspace brick-name]
  (brick-deps-table/print-table workspace brick-name))

(defn print-workspace-table [workspace]
  (workspace-deps-table/print-table workspace))

(defn print-workspace-project-table [workspace project-name]
  (workspace-project-deps-table/print-table workspace project-name))

(defn resolve-deps [project settings is-verbose]
  (lib-deps/resolve-deps project settings is-verbose))

(defn table [workspace]
  (workspace-deps-table/table workspace))
