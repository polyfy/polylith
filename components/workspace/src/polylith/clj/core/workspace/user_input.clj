(ns polylith.clj.core.workspace.user-input)

(defn enrich [{:keys [profile->settings default-profile-name]}
              {:keys [active-dev-profiles] :as user-input}]
  (if (and (-> profile->settings empty? not)
           (empty? active-dev-profiles))
    (assoc user-input :active-dev-profiles #{default-profile-name})
    user-input))
