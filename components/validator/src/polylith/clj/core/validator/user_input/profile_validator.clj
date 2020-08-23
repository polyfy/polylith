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
    (if (empty? missing)
      [true]
      [false (str "  Can't find profile" s ": " missing-msg)])))

;(set/difference #{""} #{"default"})
;
;
;(require '[dev.jocke :as z])
;(def workspace z/workspace)
;
;(def settings (:settings workspace))
;(def active-dev-profiles #{"default"})
;(def profile->settings (:profile->settings settings))
;
;(def existing (set (map first profile->settings)))
;
;(def missing (set/difference active-dev-profiles existing #{"default"}))
;
;(validate active-dev-profiles settings "none")
;
;(set/difference #{1 2} #{2 3})
