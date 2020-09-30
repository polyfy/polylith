(ns polylith.clj.core.workspace.text-table.profile)

(defn profile-sorting [profile default-profile-name]
  [(not= default-profile-name profile) profile])

(defn all-profiles [{:keys [profile-to-settings default-profile-name]}]
  (sort-by #(profile-sorting % default-profile-name)
           (map first profile-to-settings)))

(defn inactive-profiles [{:keys [profile-to-settings default-profile-name]} {:keys [active-dev-profiles]}]
  (sort-by #(profile-sorting % default-profile-name)
           (filter #(not (contains? active-dev-profiles %))
                   (map first profile-to-settings))))
