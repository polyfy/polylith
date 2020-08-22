(ns polylith.clj.core.validate.m204-path-exists-in-both-dev-and-profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn profile-warning [[profile {:keys [paths]}] dev-paths color-mode]
  (let [shared-paths (set/intersection dev-paths (set paths))
        message (str "The same path exists in both the " (color/environment "development" color-mode) " environment "
                     "and the " (color/profile profile color-mode) " development profile: "
                     (str/join ", " (map #(color/path % color-mode) shared-paths)))]
    (when (-> shared-paths empty? not)
      [(util/ordered-map :type "warning"
                         :code 204
                         :message (color/clean-colors message)
                         :colorized-message message)])))
(defn warnings [settings environments color-mode]
  (let [profile->settings (:profile->settings settings)
        {:keys [src-paths test-paths]} (common/find-environment "dev" environments)
        dev-paths (set (concat src-paths test-paths))]
    (mapcat #(profile-warning % dev-paths color-mode) profile->settings)))
