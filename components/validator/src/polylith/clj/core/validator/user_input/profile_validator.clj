(ns polylith.clj.core.validator.user-input.profile-validator
  (:require [clojure.set :as set]
            [polylith.clj.core.util.interfc.color :as color]
            [clojure.string :as str]))

(defn validate [active-dev-profiles {:keys [profile->settings]} color-mode]
  (let [existing (set (map first profile->settings))
        ;; Accept typing "+", which will give an empty active profile
        missing (set/difference active-dev-profiles existing #{""})
        s (if (= 1 (count missing)) "" "s")
        missing-msg (color/profile (str/join ", " missing) color-mode)]
    (when (-> missing empty? not)
      [(str "  Can't find profile" s ": " missing-msg)])))
