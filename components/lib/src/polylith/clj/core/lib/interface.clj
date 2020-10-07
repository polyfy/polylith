(ns polylith.clj.core.lib.interface
  (:require [polylith.clj.core.lib.core :as core])
  (:require [polylith.clj.core.lib.deps :as deps]))

(defn dependencies [settings brick]
  (deps/dependencies settings brick))

(defn with-sizes [library-map user-home]
  (core/with-sizes library-map user-home))
