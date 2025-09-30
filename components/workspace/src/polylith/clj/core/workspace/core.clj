(ns ^:no-doc polylith.clj.core.workspace.core
  (:require [polylith.clj.core.workspace.enrich.core :as enrich]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.test.interface :as test]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk]
            [polylith.clj.core.ws-file-reader.interface :as ws-file-reader]))

(defn workspace [{:keys [ws-file] :as user-input}]
  (if ws-file
    (ws-file-reader/read-ws-from-file ws-file user-input)
    (let [workspace (fromdisk/workspace-from-disk user-input)]
      (-> workspace
          (enrich/enrich-workspace)
          (change/with-changes)
          (test/with-to-test)))))
