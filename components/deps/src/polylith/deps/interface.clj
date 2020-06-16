(ns polylith.deps.interface
  (:require [polylith.deps.dependencies :as deps]
            [polylith.deps.interface-deps :as interface-deps]))

(defn brick-dependencies [top-ns interface-name interface-names brick-imports]
  (deps/brick-dependencies top-ns interface-name interface-names brick-imports))

(defn interface-deps [interfaces components]
  (interface-deps/interface-deps interfaces components))

(defn brick-interface-deps [top-ns interface-names brick]
  (deps/interface-deps top-ns interface-names brick))
