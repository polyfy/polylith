(ns polylith.clj.core.validator.user-input.profile-validator-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.user-input.profile-validator :as validator]))

(def settings {:active-profiles #{"admin"}
               :profile-to-settings {"default" {},
                                     "admin" {}}})
(deftest validate--when-the-profile-exists--return-empty-list
  (is (= nil
         (validator/validate settings color/none))))

(deftest validate--when-missing-profile--return-error
  (is (= ["  Can't find profile: adminx"]
         (validator/validate (assoc settings :active-profiles #{"adminx"}) color/none))))
