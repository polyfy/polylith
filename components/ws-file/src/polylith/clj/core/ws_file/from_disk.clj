(ns polylith.clj.core.ws-file.from-disk
  (:require [polylith.clj.core.ws-file.from-0-to-1 :as from-0-to-1]
            [polylith.clj.core.ws-file.from-1-to-2.converter :as from-1-to-2]
            [polylith.clj.core.ws-file.version-converter :as version-converter]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.common.interface :as common]))

(defn read-ws-from-file [ws-file {:keys [selected-profiles] :as user-input}]
  (let [ws-path (common/user-path ws-file)]
    (if (not (file/exists ws-path))
      (println (str "The file '" ws-path "' doesn't exist."))
      (let [ws (first (file/read-file ws-path))
            breaking (-> ws :version :ws :breaking)
            from-0-to-1? (-> ws :settings :project-to-alias)
            from-1-to-2? (and breaking (<= breaking 1))
            old-user-input (-> ws :user-input)
            old-active-profiles (-> ws :settings :active-profiles)
            old (cond-> {:user-input old-user-input}
                        (seq selected-profiles) (assoc :active-profiles old-active-profiles))]
        (cond-> (assoc ws :old old
                          :user-input user-input)
                (seq selected-profiles) (assoc-in [:settings :active-profiles] selected-profiles)
                from-0-to-1? (from-0-to-1/convert)
                from-1-to-2? (from-1-to-2/convert)
                true (version-converter/convert))))))
