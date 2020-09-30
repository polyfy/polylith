(ns polylith.clj.core.workspace.user-input)

(defn enrich [{:keys [profile-to-settings default-profile-name]}
              {:keys [active-dev-profiles] :as user-input}]
  (if (and (-> profile-to-settings empty? not)
           (empty? active-dev-profiles))
    (assoc user-input :active-dev-profiles #{default-profile-name})
    (if (= #{""} active-dev-profiles)
      ;; The user has typed e.g. "poly info +"
      (assoc user-input :active-dev-profiles #{})
      user-input)))
