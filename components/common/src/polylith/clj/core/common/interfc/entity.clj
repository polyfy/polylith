(ns polylith.clj.core.common.interfc.entity
  (:require [polylith.clj.core.common.entity :as brick]))

(defn components-from-paths [paths]
  (brick/components-from-paths paths))

(defn bases-from-paths [paths]
  (brick/bases-from-paths paths))

(defn bricks-from-paths [paths]
  (brick/bricks-from-paths paths))

(defn environments-from-paths [paths]
  (brick/environments-from-paths paths))
