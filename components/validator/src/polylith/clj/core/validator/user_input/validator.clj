(ns polylith.clj.core.validator.user-input.validator
  (:require [polylith.clj.core.validator.user-input.profile-validator :as profile]))

(defn validate [active-dev-profiles settings color-mode]
  (profile/validate active-dev-profiles settings color-mode))
