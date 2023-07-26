(ns ^:no-doc polylith.clj.core.validator.m204-lib-deps-exists-in-both-dev-and-profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn profile-warning [[profile {:keys [lib-deps]}] dev-lib-deps color-mode]
  (let [shared-lib-deps (sort (set/intersection dev-lib-deps (set (map first lib-deps))))
        libraries (str/join "," (map #(color/library % color-mode) shared-lib-deps))
        message (str "The same library dependency exists in both the " (color/project "development" color-mode) " project "
                     "and the " (color/profile profile color-mode) " profile: " libraries)]
    (when (-> shared-lib-deps empty? not)
      [(util/ordered-map :type "warning"
                         :code 204
                         :message (color/clean-colors message)
                         :colorized-message message)])))
(defn warnings [settings projects
                color-mode]
  (let [profile-to-settings (:profile-to-settings settings)
        {:keys [unmerged]} (common/find-project "dev" projects)
        {:keys [lib-deps]} unmerged
        dev-lib-deps (set (map first (concat (:src lib-deps)
                                             (:test lib-deps))))]
    (mapcat #(profile-warning % dev-lib-deps color-mode) profile-to-settings)))
