(ns polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn profile-warning [[profile {:keys [paths]}] dev-paths color-mode]
  (let [shared-paths (set/intersection dev-paths (set paths))
        message (str "The same path exists in both the " (color/environment "development" color-mode) " environment "
                     "and the " (color/profile profile color-mode) " profile: "
                     (str/join ", " (map #(color/path % color-mode) shared-paths)))]
    (when (-> shared-paths empty? not)
      [(util/ordered-map :type "warning"
                         :code 203
                         :message (color/clean-colors message)
                         :colorized-message message)])))
(defn warnings [settings environments color-mode]
  (let [profile-to-settings (:profile-to-settings settings)
        {:keys [src-paths test-paths]} (common/find-environment "dev" environments)
        dev-paths (set (concat src-paths test-paths))]
    (mapcat #(profile-warning % dev-paths color-mode) profile-to-settings)))
