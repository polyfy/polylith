(ns ^:no-doc polylith.clj.core.workspace.interface
  (:require [polylith.clj.core.workspace.enrich.core :as enrich]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk])
  (:gen-class))

(defn workspace [user-input]
  (-> user-input
      fromdisk/workspace-from-disk
      enrich/enrich-workspace
      change/with-changes))
