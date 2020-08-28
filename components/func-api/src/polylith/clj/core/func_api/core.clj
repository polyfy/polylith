(ns polylith.clj.core.func-api.core
  (:require [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.change.interfc :as change]
            [polylith.clj.core.user-input.interfc :as user-input]
            [polylith.clj.core.workspace.interfc :as ws]
            [polylith.clj.core.workspace-clj.interfc :as ws-clj]))

(defn changed-environments []
  (let [exists? (file/exists (str "./deps.edn"))
        user-input (user-input/extract-params [])
        workspace (when exists? (-> "."
                                    ws-clj/workspace-from-disk
                                    (ws/enrich-workspace user-input)
                                    change/with-changes))]
    (filterv #(not= "development" %)
             (-> workspace :changes :changed-environments))))
