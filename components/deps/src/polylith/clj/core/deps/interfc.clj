(ns polylith.clj.core.deps.interfc
  (:require [polylith.clj.core.deps.dependencies :as deps]))

(defn brick-dependencies [top-ns interface-name interface-names brick-namespaces]
  (deps/brick-dependencies top-ns interface-name interface-names brick-namespaces))

(defn brick-interface-deps [top-ns interface-names brick]
  (deps/interface-deps top-ns interface-names brick))
