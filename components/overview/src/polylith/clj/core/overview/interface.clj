(ns polylith.clj.core.overview.interface
  (:require [polylith.clj.core.overview.core :as core]))

(defn create-image [workspace]
  (core/create-image workspace))