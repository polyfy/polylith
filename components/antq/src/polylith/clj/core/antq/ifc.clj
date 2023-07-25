;; This interface has the name ifc, just to check that it's allowed!
(ns ^:no-doc polylith.clj.core.antq.ifc
  (:require [polylith.clj.core.antq.core :as core]))

(defn library->latest-version [configs]
  (core/library->latest-version configs))
