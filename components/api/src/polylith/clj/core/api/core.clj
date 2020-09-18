(ns polylith.clj.core.api.core
  (:require [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]))

(defn changed-environments []
  (let [user-input (user-input/extract-params [])
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-last-build-changes)]
    (filterv #(not= "development" %)
             (-> workspace :changes :changed-or-affected-environments))))
