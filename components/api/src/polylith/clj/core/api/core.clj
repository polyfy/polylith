(ns polylith.clj.core.api.core
  (:require [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.user-input.interfc :as user-input]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]))

(defn changed-environments []
  (let [user-input (user-input/extract-params [])
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-changes)]
    (filterv #(not= "development" %)
             (-> workspace :changes :changed-environments))))
