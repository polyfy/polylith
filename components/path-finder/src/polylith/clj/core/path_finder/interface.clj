(ns ^:no-doc polylith.clj.core.path-finder.interface
  (:require [polylith.clj.core.path-finder.paths :as paths]))

(defn paths [ws-dir projects profiles]
  (paths/paths ws-dir projects profiles))
