(ns polylith.clj.core.antq.interface
  (:require [polylith.clj.core.antq.core :as core]))

(defn library->latest-version [configs]
  (core/library->latest-version configs))
