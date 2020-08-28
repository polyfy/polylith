(ns polylith.clj.core.lib-dep.interfc
  (:require [polylith.clj.core.lib-dep.core :as core]))

(defn dependencies [settings brick]
  (core/dependencies settings brick))
