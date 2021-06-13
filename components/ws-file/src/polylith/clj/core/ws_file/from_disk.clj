(ns polylith.clj.core.ws-file.from-disk
  (:require [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.ws-file.from-1-to-2 :as from1to2]))

(defn read-ws-from-file [ws-file {:keys [selected-profiles] :as user-input}]
  (if (not (file/exists ws-file))
    (println (str "The file '" ws-file "' doesn't exist."))
    (let [ws (first (file/read-file ws-file))
          project-to-alias (-> ws :settings :project-to-alias)
          old-user-input (-> ws :user-input)
          old-active-profiles (-> ws :settings :active-profiles)
          old (cond-> {:user-input old-user-input}
                      (seq selected-profiles) (assoc :active-profiles old-active-profiles))]
      (cond-> (assoc ws :old old
                        :user-input user-input)
              (seq selected-profiles) (assoc-in [:settings :active-profiles] selected-profiles)
              project-to-alias (from1to2/convert)))))
