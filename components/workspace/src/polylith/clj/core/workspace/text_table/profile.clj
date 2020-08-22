(ns polylith.clj.core.workspace.text-table.profile)

(defn profile-sorting [profile]
  [(not= "default" profile) profile])

(defn all-profiles [{:keys [profile->settings]}]
  (sort-by profile-sorting
           (map first profile->settings)))

(defn inactive-profiles [{:keys [profile->settings]} {:keys [active-dev-profiles]}]
  (sort-by profile-sorting
           (filter #(not (contains? active-dev-profiles %))
                   (map first profile->settings))))
