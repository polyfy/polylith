(ns polylith.clj.core.api.core
  (:require [clojure.string :as str]
            [polylith.clj.core.change.interface :as change]
            [polylith.clj.core.user-input.interface :as user-input]
            [polylith.clj.core.workspace.interface :as ws]
            [polylith.clj.core.workspace-clj.interface :as ws-clj]
            [polylith.clj.core.ws-explorer.interface :as ws-explorer]))

(defn environments-to-deploy []
  "Returns the environments that have been affected since last deploy,
   tagged in git following the pattern defined by :build-tag-pattern in
   deps.edn, or v* if not defined."
  (let [user-input (user-input/extract-params [])
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-last-build-changes)]
    (filterv #(not= "development" %)
             (-> workspace :changes :changed-or-affected-environments))))

(defn workspace [keys]
  "Returns the workspace or part of the workspace by sending in a comma-separated
   list of keys, with the same format as the 'ws' command. If keys is nil or blank,
   return the whole workspace"
  (let [args (when keys ["ws" (str "get:" keys)] ["ws"])
        user-input (user-input/extract-params args)
        workspace (-> user-input
                      ws-clj/workspace-from-disk
                      ws/enrich-workspace
                      change/with-last-build-changes)]
    (if (str/blank? keys)
      workspace
      (ws-explorer/extract workspace (str/split keys #":")))))
