(ns polylith.clj.core.command.cmd-validator.profile
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn validate [{:keys [profile-to-settings active-profiles]} color-mode]
  (let [existing (set (map first profile-to-settings))
        missing (set/difference active-profiles existing)
        s (if (= 1 (count missing)) "" "s")
        missing-msg (color/profile (str/join ", " missing) color-mode)]
    (when (-> missing empty? not)
      [(str "  Can't find profile" s ": " missing-msg)])))
