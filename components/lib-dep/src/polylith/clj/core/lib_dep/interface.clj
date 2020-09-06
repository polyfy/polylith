(ns polylith.clj.core.lib-dep.interface
  (:require [polylith.clj.core.lib-dep.core :as core]))

(defn dependencies [settings brick]
  (core/dependencies settings brick))
