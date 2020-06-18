(ns polylith.deps.interface
  (:require [polylith.deps.dependencies :as deps]
            [polylith.deps.interface-deps :as interface-contracts]))

(defn brick-dependencies [top-ns interface-name interface-names brick-namespaces]
  (deps/brick-dependencies top-ns interface-name interface-names brick-namespaces))

(defn interface-deps [interfaces components]
  (interface-contracts/dependencies interfaces components))

(defn brick-interface-deps [top-ns interface-names brick]
  (deps/interface-deps top-ns interface-names brick))
