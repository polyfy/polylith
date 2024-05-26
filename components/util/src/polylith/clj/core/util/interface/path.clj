(ns polylith.clj.core.util.interface.path
  (:require [polylith.clj.core.util.path :as path]))

(defn relative-path
  "Returns a relative path to the base path."
  [current-path path]
  (path/relative-path current-path path))
