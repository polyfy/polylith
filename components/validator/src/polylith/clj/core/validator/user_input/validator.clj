(ns polylith.clj.core.validator.user-input.validator
  (:require [polylith.clj.core.validator.user-input.env-validator :as env])
  (:require [polylith.clj.core.validator.user-input.profile-validator :as profile]))

(defn validate [selected-environments
                settings
                environments
                color-mode]
  (let [messages (concat (profile/validate settings color-mode)
                         (env/validate selected-environments environments color-mode))]
    (if (empty? messages)
       [true]
       [false (first messages)])))
