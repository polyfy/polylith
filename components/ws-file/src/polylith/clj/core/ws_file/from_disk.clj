(ns ^:no-doc polylith.clj.core.ws-file.from-disk
  (:require [polylith.clj.core.ws-file.from-0-to-1 :as from-0-to-1]
            [polylith.clj.core.ws-file.from-1-to-2.converter :as from-1-to-2]
            [polylith.clj.core.ws-file.from-2-to-3.converter :as from-2-to-3]
            [polylith.clj.core.ws-file.from-3-to-4.converter :as from-3-to-4]
            [polylith.clj.core.ws-file.from-4-to-5.converter :as from-4-to-5]
            [polylith.clj.core.ws-file.version-converter :as version-converter]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]))

(defn read-ws-from-file 
  [ws-file {:keys [selected-profiles is-no-changes color-mode] :as user-input}]
  (let [ws-path (common/user-path ws-file)]
    (if (not (file/exists ws-path))
      (println (str "The file '" ws-path "' doesn't exist."))
      (let [ws (first (file/read-file ws-path #{"clj"}))
            breaking (or (-> ws :version :ws :breaking) 0)
            from-0-to-1? (-> ws :settings :project-to-alias)
            from-1-to-2? (<= breaking 1)
            from-2-to-3? (<= breaking 2)
            ;; We forgot to increase to 4, that's why we need to check it programmatically
            from-3-to-4? (and (<= breaking 3) (-> ws :configs :workspaces))
            from-4-to-5? (and (<= breaking 3) (-> ws :ws-dialects not))
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
                              from-3-to-4? (from-3-to-4/convert)
                              from-4-to-5? (from-4-to-5/convert)
                              true (version-converter/convert))]
        (if is-no-changes
          (-> workspace
              (assoc-in [:changes :changed-files] [])
              (assoc-in [:changes :changed-bases] [])
              (assoc-in [:changes :changed-components] [])
              (assoc-in [:changes :changed-projects] [])
              (assoc-in [:changes :changed-or-affected-projects] []))
          workspace)))))
