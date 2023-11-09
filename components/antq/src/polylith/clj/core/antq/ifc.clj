;; This interface has the name ifc, just to check that it's allowed!
(ns ^:no-doc polylith.clj.core.antq.ifc
  (:require [polylith.clj.core.antq.latest :as latest]
            [polylith.clj.core.antq.upgrade :as upgrade]))

(defn library->latest-version [configs]
  (latest/library->latest-version configs))

(defn upgrade-all-libs! [workspace color-mode]
  (upgrade/upgrade-all-libs! workspace color-mode))
