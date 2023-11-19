;; This interface has the name ifc, just to check that it works!
(ns ^:no-doc polylith.clj.core.antq.ifc
  (:require [polylith.clj.core.antq.outdated :as outdated]
            [polylith.clj.core.antq.upgrade :as upgrade]))

(defn library->latest-version [configs calculate?]
  (outdated/library->latest-version configs calculate?))

(defn upgrade-lib [ws-dir color-mode entity-type entity-name lib]
  (upgrade/upgrade-lib ws-dir color-mode entity-type entity-name lib))
