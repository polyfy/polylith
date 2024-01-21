(ns ^:no-doc polylith.clj.core.common.profile)

(defn profile-sorting [{:keys [name]} default-profile-name]
  [(not= default-profile-name name) name])

(defn sort-profiles [default-profile-name profiles]
  (sort-by #(profile-sorting % default-profile-name)
           profiles))
