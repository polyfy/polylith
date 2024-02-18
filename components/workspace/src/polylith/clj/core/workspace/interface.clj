(ns ^:no-doc polylith.clj.core.workspace.interface
  (:require [polylith.clj.core.workspace.enrich.core :as enrich]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk]
            [polylith.clj.core.ws-file.interface :as ws-file])
  (:gen-class))

(defn workspace [{:keys [ws-file] :as user-input}]
  (if ws-file
    (ws-file/read-ws-from-file ws-file user-input)
    (-> user-input
        fromdisk/workspace-from-disk
        enrich/enrich-workspace
        change/with-changes)))
