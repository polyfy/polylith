(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]))

(defn profile [[profile-key {:keys [extra-paths extra-deps]}] user-home]
  (let [path-entries (extract/path-entries [extra-paths] nil)
        component-names (vec (sort (select/names path-entries c/component?)))
        base-names (vec (sort (select/names path-entries c/base?)))
        environment-names (vec (sort (select/names path-entries c/environment?)))]

    [(subs (name profile-key) 1)
     (util/ordered-map :paths extra-paths
                       :lib-deps (lib/with-sizes extra-deps user-home)
                       :component-names component-names
                       :base-names base-names
                       :environment-names environment-names)]))

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
