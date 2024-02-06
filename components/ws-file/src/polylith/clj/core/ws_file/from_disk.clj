(ns ^:no-doc polylith.clj.core.ws-file.from-disk
  (:require [polylith.clj.core.ws-file.from-0-to-1 :as from-0-to-1]
            [polylith.clj.core.ws-file.from-1-to-2.converter :as from-1-to-2]
            [polylith.clj.core.ws-file.from-2-to-3.converter :as from-2-to-3]
            [polylith.clj.core.ws-file.version-converter :as version-converter]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]))

(defn read-ws-from-file [ws-file {:keys [selected-profiles is-no-changes color-mode] :as user-input}]
  (let [ws-path (common/user-path ws-file)]
    (if (not (file/exists ws-path))
      (println (str "The file '" ws-path "' doesn't exist."))
      (let [ws (first (file/read-file ws-path))
            breaking (or (-> ws :version :ws :breaking) 0)
            from-0-to-1? (-> ws :settings :project-to-alias)
            from-1-to-2? (<= breaking 1)
            from-2-to-3? (<= breaking 2)
            old-user-input (-> ws :user-input)
            old-active-profiles (-> ws :settings :active-profiles)
            old (cond-> {:user-input old-user-input}
                        (seq selected-profiles) (assoc :active-profiles old-active-profiles))
            workspace (cond-> (assoc ws :old old
                                        :user-input user-input)
                              (seq selected-profiles) (assoc-in [:settings :active-profiles] selected-profiles)
                              color-mode (assoc-in [:settings :color-mode] color-mode)
                              from-0-to-1? (from-0-to-1/convert)
                              from-1-to-2? (from-1-to-2/convert)
                              from-2-to-3? (from-2-to-3/convert)
                              true (version-converter/convert))]
        (if is-no-changes
          (-> workspace
              (assoc-in [:changes :changed-files] [])
              (assoc-in [:changes :changed-bases] [])
              (assoc-in [:changes :changed-components] [])
              (assoc-in [:changes :changed-projects] [])
              (assoc-in [:changes :changed-or-affected-projects] []))
          workspace)))))
