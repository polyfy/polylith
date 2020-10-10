(ns polylith.clj.core.deps.interface
  (:require [polylith.clj.core.deps.interface-deps :as ifc-deps]
            [polylith.clj.core.deps.text-table.brick-ifc-deps-table :as brick-ifc-deps-table]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.workspace-ifc-deps-table :as ws-ifc-deps-table]
            [polylith.clj.core.deps.text-table.workspace-brick-deps-table :as ws-brick-deps-table]))

(defn interface-ns-deps [suffixed-top-ns interface-name interface-names brick-namespaces]
  (ifc-deps/interface-ns-deps suffixed-top-ns interface-name interface-names brick-namespaces))

(defn interface-deps [suffixed-top-ns interface-names brick]
  (ifc-deps/interface-deps suffixed-top-ns interface-names brick))

(defn print-brick-table [workspace environment-name brick-name]
  (brick-deps-table/print-table workspace environment-name brick-name))

(defn print-brick-ifc-table [workspace brick-name]
  (brick-ifc-deps-table/print-table workspace brick-name))

(defn print-workspace-ifc-table [workspace]
  (ws-ifc-deps-table/print-table workspace))

(defn print-workspace-brick-table [workspace environment-name]
  (ws-brick-deps-table/print-table workspace environment-name))
