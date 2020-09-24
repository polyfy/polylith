(ns polylith.clj.core.change.interface
  (:require [polylith.clj.core.change.core :as core]))

(defn with-last-stable-changes [workspace]
  (core/with-last-stable-changes workspace))

(defn with-previous-build-changes [workspace]
  (core/with-previous-build-changes workspace))
