(ns ^:no-doc polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color]))

(defn profile-warning [{:keys [name paths]} dev-paths color-mode]
  (let [shared-paths (set/intersection dev-paths (set paths))
        message (str "The same path exists in both the " (color/project "development" color-mode) " project "
                     "and the " (color/profile name color-mode) " profile: "
                     (str/join ", " (map #(color/path % color-mode) shared-paths)))]
    (when (-> shared-paths empty? not)
      [(util/ordered-map :type "warning"
                         :code 203
                         :message (color/clean-colors message)
                         :colorized-message message)])))
(defn warnings [profiles projects color-mode]
  (let [{:keys [unmerged]} (common/find-project "dev" projects)
        {:keys [paths]} unmerged
        dev-paths (set (concat (:src paths) (:test paths)))]
    (mapcat #(profile-warning % dev-paths color-mode) profiles)))
