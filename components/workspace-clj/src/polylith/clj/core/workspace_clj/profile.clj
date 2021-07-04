(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]))

(defn profile [[profile-key {:keys [extra-paths extra-deps]}] user-home]
  (let [path-entries (extract/from-paths {:src extra-paths} nil)
        component-names (vec (sort (select/names path-entries c/component?)))
        base-names (vec (sort (select/names path-entries c/base?)))
        project-names (vec (sort (select/names path-entries c/project?)))]

    [(subs (name profile-key) 1)
     (util/ordered-map :paths extra-paths
                       :lib-deps (lib/latest-with-sizes extra-deps user-home)
                       :component-names component-names
                       :base-names base-names
                       :project-names project-names)]))

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
