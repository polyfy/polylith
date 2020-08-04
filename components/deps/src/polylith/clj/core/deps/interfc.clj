(ns polylith.clj.core.deps.interfc
  (:require [polylith.clj.core.deps.interface-deps :as ifc-deps]
            [polylith.clj.core.deps.text-table.brick-ifc-deps-table :as brick-ifc-deps-table]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-deps-table]
            [polylith.clj.core.deps.text-table.workspace-ifc-deps-table :as ws-ifc-deps-table]
            [polylith.clj.core.deps.text-table.workspace-brick-deps-table :as ws-brick-deps-table]))

(defn interface-ns-deps [top-ns interface-name interface-names brick-namespaces]
  (ifc-deps/interface-ns-deps top-ns interface-name interface-names brick-namespaces))

(defn interface-deps [suffixed-top-ns interface-names brick]
  (ifc-deps/interface-deps suffixed-top-ns interface-names brick))

(defn print-brick-table [workspace environment-name brick-name color-mode]
  (brick-deps-table/print-table workspace environment-name brick-name color-mode))

(defn print-brick-ifc-table [workspace brick-name color-mode]
  (brick-ifc-deps-table/print-table workspace brick-name color-mode))

(defn print-workspace-ifc-table [workspace color-mode]
  (ws-ifc-deps-table/print-table workspace color-mode))

(defn print-workspace-brick-table [workspace environment-name color-mode]
  (ws-brick-deps-table/print-table workspace environment-name color-mode))
