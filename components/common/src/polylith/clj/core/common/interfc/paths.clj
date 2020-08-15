(ns polylith.clj.core.common.interfc.paths
  (:require [polylith.clj.core.common.paths :as paths]))

(defn src-path? [path]
  (paths/src-path? path))

(defn test-path? [path]
  (paths/test-path? path))

(defn component-paths [paths]
  (paths/component-paths paths))

(defn base-paths [paths]
  (paths/base-paths paths))

(defn components-from-paths [paths]
  (paths/components-from-paths paths))

(defn bases-from-paths [paths]
  (paths/bases-from-paths paths))

(defn bricks-from-paths [paths]
  (paths/bricks-from-paths paths))

(defn environments-from-paths [paths]
  (paths/environments-from-paths paths))
