(ns polylith.clj.core.validator.user-input.profile-validator-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validator.user-input.profile-validator :as validator]))

(def settings {:profile->settings {"default" {},
                                   "admin" {}}})
(deftest validate--when-the-profile-exists--return-empty-list
  (is (= []
         (validator/validate #{"admin"} settings color/none))))

(deftest validate--when-missing-profile--return-error
  (is (= ["  Can't find profile: adminx"]
         (validator/validate #{"adminx"} settings color/none))))
