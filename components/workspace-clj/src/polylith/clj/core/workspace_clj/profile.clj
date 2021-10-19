(ns polylith.clj.core.workspace-clj.profile
  (:require [clojure.string :as str]
            [polylith.clj.core.lib.interface :as lib]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.path-finder.interface.criterias :as c]
            [polylith.clj.core.path-finder.interface.extract :as extract]
            [polylith.clj.core.path-finder.interface.select :as select]
            [polylith.clj.core.util.interface.str :as str-util]))

(defn brick-name [[_ {:keys [local/root]}]]
  (when root
    (cond
      (str/starts-with? root "bases/") (str-util/skip-prefix root "bases/")
      (str/starts-with? root "components/") (str-util/skip-prefix root "components/"))))

(defn brick-name [[_ {:keys [local/root]}] bricks-path]
  (when (and root
             (str/starts-with? root bricks-path))
    [(str-util/skip-prefix root bricks-path)]))

(defn lib? [dep]
  (empty? (concat (brick-name dep "bases/")
                  (brick-name dep "components/"))))

(defn ->brick-paths [{:keys [name type paths]}]
  (map #(str type "s/" name "/" %)
       (concat (:src paths)
               (:test paths))))

(defn profile [ws-dir [profile-key {:keys [extra-paths extra-deps]}] name->brick user-home]
  (let [;; :extra-paths
        path-entries (extract/from-paths {:src extra-paths} nil)
        path-base-names (vec (sort (select/names path-entries c/base?)))
        path-component-names (vec (sort (select/names path-entries c/component?)))
        project-names (vec (sort (select/names path-entries c/project?)))
        ;; extra-deps
        deps-base-names (mapcat #(brick-name % "bases/") extra-deps)
        deps-component-names (mapcat #(brick-name % "components/") extra-deps)
        deps-brick-names (concat deps-base-names deps-component-names)
        deps-brick-paths (mapcat #(-> % name->brick ->brick-paths) deps-brick-names)
        ;; result
        base-names (vec (sort (set (concat path-base-names deps-base-names))))
        component-names (vec (sort (set (concat path-component-names deps-component-names))))
        paths (vec (sort (set (concat extra-paths deps-brick-paths))))
        lib-deps (lib/latest-with-sizes ws-dir nil
                                        (filter lib? extra-deps)
                                        user-home)]
    [(subs (name profile-key) 1)
     (util/ordered-map :paths paths
                       :lib-deps lib-deps
                       :component-names component-names
                       :base-names base-names
                       :project-names project-names)]))

(defn profile? [[alias]]
  (str/starts-with? (name alias) "+"))

(defn profile-to-settings [ws-dir aliases name->brick user-home]
  (into {} (map #(profile ws-dir % name->brick user-home)
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
