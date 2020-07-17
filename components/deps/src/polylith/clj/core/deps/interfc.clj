(ns polylith.clj.core.deps.interfc
  (:require [polylith.clj.core.deps.interface-deps :as ifc-deps]
            [polylith.clj.core.deps.brick-deps-table :as brick-deps-table]))

(defn interface-ns-deps [top-ns interface-name interface-names brick-namespaces]
  (ifc-deps/interface-ns-deps top-ns interface-name interface-names brick-namespaces))

(defn interface-deps [top-ns interface-names brick]
  (ifc-deps/interface-deps top-ns interface-names brick))

(defn print-brick-table [workspace environment-name brick-name color-mode]
  (brick-deps-table/print-table workspace environment-name brick-name color-mode))
