(ns polylith.clj.core.validator.m205-lib-deps-exists-in-both-dev-and-profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.common.interfc :as common]
            [polylith.clj.core.util.interfc :as util]
            [polylith.clj.core.util.interfc.color :as color]))

(defn profile-warning [[profile {:keys [lib-deps]}] dev-lib-deps color-mode]
  (let [shared-lib-deps (sort (set/intersection dev-lib-deps (set (map first lib-deps))))
        libraries (str/join "," (map #(color/library % color-mode) shared-lib-deps))
        message (str "The same library dependency exists in both the " (color/environment "development" color-mode) " environment "
                     "and the " (color/profile profile color-mode) " development profile: " libraries)]
    (when (-> shared-lib-deps empty? not)
      [(util/ordered-map :type "warning"
                         :code 205
                         :message (color/clean-colors message)
                         :colorized-message message)])))
(defn warnings [settings environments color-mode]
  (let [profile->settings (:profile->settings settings)
        {:keys [lib-deps test-lib-deps]} (common/find-environment "dev" environments)
        dev-lib-deps (set (map first (concat lib-deps test-lib-deps)))]
    (mapcat #(profile-warning % dev-lib-deps color-mode) profile->settings)))
