(ns polylith.clj.core.maven.interface
  (:require [polylith.clj.core.maven.core :as core]))

(defn latest-lib [coord1 coord2 mvn-key]
  (core/latest-lib coord1 coord2 mvn-key))

(defn oldest-lib [coord1 coord2 mvn-key]
  (core/oldest-lib coord1 coord2 mvn-key))
