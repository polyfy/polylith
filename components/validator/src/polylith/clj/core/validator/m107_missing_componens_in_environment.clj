(ns polylith.clj.core.validator.m107-missing-componens-in-environment
  (:require [clojure.string :as str]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color])
  (:refer-clojure :exclude [bases]))

(defn missing-components-error [env interface-names color-mode]
  (let [interfaces (str/join ", " (sort interface-names))
        message (str "Missing components in the " (color/environment env color-mode) " environment "
                     "for these interfaces: " (color/interface interfaces color-mode))]
    [(util/ordered-map :type "error"
                       :code 107
                       :message (color/clean-colors message)
                       :colorized-message message
                       :interfaces interface-names
                       :environment env)]))

(defn env-error [{:keys [name deps]} color-mode]
  (let [missing (vec (mapcat :direct-ifc (map second deps)))]
    (when (-> missing empty? not)
      (missing-components-error name missing color-mode))))

(defn show-error? [profile->settings active-dev-profiles]
  ;; When we have at least one profile and the user has deselected all active
  ;; profiles by sending in "+" as an argument, then don't show this error.
  (or (-> profile->settings empty?)
      (-> active-dev-profiles empty? not)))

(defn errors [{:keys [profile->settings]} environments active-dev-profiles color-mode]
  (when (show-error? profile->settings active-dev-profiles)
    (mapcat #(env-error % color-mode)
            environments)))
