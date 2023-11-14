;; This interface has the name ifc, just to check that it works!
(ns ^:no-doc polylith.clj.core.antq.ifc
  (:require [polylith.clj.core.antq.outdated :as outdated]
            [polylith.clj.core.antq.upgrade :as upgrade]))

(defn library->latest-version [configs]
  (outdated/library->latest-version configs))

(defn upgrade-libs!
  "If libs-to-update is left empty, then update all libs."
  [workspace libraries-to-update type->name->lib->version color-mode]
  (upgrade/upgrade-libs! workspace libraries-to-update type->name->lib->version color-mode))
