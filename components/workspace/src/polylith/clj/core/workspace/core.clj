(ns ^:no-doc polylith.clj.core.workspace.core
  (:require [polylith.clj.core.workspace.enrich.core :as enrich]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.test.interface :as test]
            [polylith.clj.core.workspace.external.fromdisk :as external-from-disk]
            [polylith.clj.core.workspace.fromdisk.core :as fromdisk]
            [polylith.clj.core.ws-file.interface :as ws-file]))

(defn workspace [{:keys [ws-file] :as user-input}]
  (if ws-file
    (ws-file/read-ws-from-file ws-file user-input)
    (let [{:keys [ws-dir] :as workspace} (fromdisk/workspace-from-disk user-input)]
      (when-let [workspaces (external-from-disk/workspaces ws-dir user-input)]
        (-> workspace
            (enrich/enrich-workspace workspaces)
            (change/with-changes)
            (test/with-to-test))))))

(comment
  (require '[polylith.clj.core.user-input.interface :as user-input])
  (def input (user-input/extract-arguments ["info" "ws-dir:examples/multiple-workspaces2/backend"]))

  (def ws (workspace input))
  #__)
