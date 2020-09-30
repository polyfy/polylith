(ns polylith.clj.core.validator.user-input.profile-validator
  (:require [clojure.set :as set]
            [clojure.string :as str]
            [polylith.clj.core.util.interface.color :as color]))

(defn validate [active-dev-profiles {:keys [profile-to-settings]} color-mode]
  (let [existing (set (map first profile-to-settings))
        ;; Accept typing "+", which will give an empty active profile
        missing (set/difference active-dev-profiles existing #{""})
        s (if (= 1 (count missing)) "" "s")
        missing-msg (color/profile (str/join ", " missing) color-mode)]
    (when (-> missing empty? not)
      [(str "  Can't find profile" s ": " missing-msg)])))
