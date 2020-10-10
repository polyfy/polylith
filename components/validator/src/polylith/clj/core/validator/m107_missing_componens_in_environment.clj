(ns polylith.clj.core.validator.m107-missing-componens-in-environment
  (:require [clojure.string :as str]
            [polylith.clj.core.validator.shared :as shared]
            [polylith.clj.core.util.interface :as util]
            [polylith.clj.core.util.interface.color :as color])
  (:refer-clojure :exclude [bases]))

(defn missing-components-error [env interface-names color-mode]
  (let [interfaces (str/join ", " (sort (set interface-names)))
        message (str "Missing components in the " (color/environment env color-mode) " environment "
                     "for these interfaces: " (color/interface interfaces color-mode))]
    [(util/ordered-map :type "error"
                       :code 107
                       :message (color/clean-colors message)
                       :colorized-message message
                       :interfaces interface-names
                       :environment env)]))

(defn env-error [{:keys [name deps]} color-mode]
  (let [missing (mapcat :direct-ifc (map second deps))]
    (when (-> missing empty? not)
      (missing-components-error name missing color-mode))))

(defn errors [cmd {:keys [profile-to-settings active-profiles]} environments color-mode]
  (when (shared/show-error? cmd profile-to-settings active-profiles)
    (mapcat #(env-error % color-mode)
            environments)))
