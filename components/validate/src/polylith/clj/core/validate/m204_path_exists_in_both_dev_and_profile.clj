(ns polylith.clj.core.validate.m204-path-exists-in-both-dev-and-profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]))

(defn profile-warning [[profile {:keys [paths]}] dev-paths]
  (let [shared-paths (set/intersection dev-paths (set paths))
        message (str "The same path exists in both the development environment and the '" profile "' profile: "
                     (str/join ", " shared-paths))]
    (when (-> shared-paths empty? not)
      [(util/ordered-map :type "warning"
                         :code 204
                         :message message
                         :colorized-message message)])))
(defn warnings [settings environments]
  (let [profile->settings (:profile->settings settings)
        {:keys [src-paths test-paths]} (common/find-environment "dev" environments)
        dev-paths (set (concat src-paths test-paths))]
    (mapcat #(profile-warning % dev-paths) profile->settings)))
