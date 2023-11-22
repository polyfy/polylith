(ns polylith.clj.core.command.cmd-validator.profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.command.cmd-validator.profile :as profile]))

(def settings {:active-profiles #{"admin"}
               :profile-to-settings {"default" {},
                                     "admin" {}}})
(deftest validate--when-the-profile-exists--return-empty-list
  (is (= nil
         (profile/validate settings color/none))))

(deftest validate--when-missing-profile--return-error
  (is (= ["  Can't find profile: adminx"]
         (profile/validate (assoc settings :active-profiles #{"adminx"}) color/none))))
