(ns polylith.clj.core.change.interface
  (:require [polylith.clj.core.change.core :as core]))

(defn with-changes [workspace]
  (core/with-changes workspace))
