(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]))

(defn profile [[profile-key {:keys [extra-paths extra-deps]}] user-home]
  [(subs (name profile-key) 1)
   (util/ordered-map :paths extra-paths
                     :lib-deps (lib/with-sizes extra-deps user-home))])

(defn profile? [[alias]]
  (str/starts-with? (name alias) "+"))

(defn profile-to-settings [aliases user-home]
  (into {} (map #(profile % user-home)
                (filterv profile? aliases))))

(defn active-profiles [{:keys [selected-profiles]}
                       default-profile-name
                       profile-to-settings]
  (if (empty? selected-profiles)
    (if (empty? profile-to-settings)
      #{}
      #{default-profile-name})
    (if (contains? (set selected-profiles) "")
      []
      (set selected-profiles))))
