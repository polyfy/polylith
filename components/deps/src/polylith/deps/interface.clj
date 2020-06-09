(ns polylith.deps.interface
  (:require [polylith.deps.dependencies :as deps]
            [polylith.deps.interface-deps :as interface-contracts]))

(defn brick-dependencies [top-ns interface-name interface-names brick-imports]
  (deps/brick-dependencies top-ns interface-name interface-names brick-imports))

(defn interface-deps [interfaces components]
  (interface-contracts/dependencies interfaces components))

(defn with-deps [top-ns interface-names brick]
  (deps/with-deps top-ns interface-names brick))
