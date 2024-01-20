(ns ^:no-doc polylith.clj.core.workspace.text-table.profile)

(defn profile-sorting [profile-name default-profile-name]
  [(not= default-profile-name profile-name) profile-name])

(defn inactive-profiles [{:keys [active-profiles default-profile-name]} profiles]
  (sort-by #(profile-sorting % default-profile-name)
           (filter #(not (contains? active-profiles (:name %)))
                   profiles)))
