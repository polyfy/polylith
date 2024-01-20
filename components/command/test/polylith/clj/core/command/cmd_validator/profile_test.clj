(ns polylith.clj.core.command.cmd-validator.profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.command.cmd-validator.profile :as profile]))

(def settings {:active-profiles #{"admin"}})

(def profiles [{:name "default"}
               {:name "admin"}])

(deftest validate--when-the-profile-exists--return-empty-list
  (is (= nil
         (profile/validate profiles settings color/none))))

(deftest validate--when-missing-profile--return-error
  (is (= ["  Can't find profile: adminx"]
         (profile/validate profiles (assoc settings :active-profiles #{"adminx"}) color/none))))
