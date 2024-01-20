(ns polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile :as m203]))

(def profiles [{:name "default"
                :type "profile"
                :paths ["components/user/src"
                        "components/user/test"]}
               {:name "admin"
                :type "profile"
                :paths ["components/admin/src"
                        "components/admin/test"
                        "components/invoice/src"]}])

(def projects [{:alias "dev"
                :unmerged {:paths {:src ["components/invoice/src"
                                         "development/src"]}}}])

(deftest warnings--path-was-found-in-both-dev-and-a-profile--returns-error-message
  (is (= [{:code 203
           :type "warning"
           :message "The same path exists in both the development project and the admin profile: components/invoice/src"
           :colorized-message "The same path exists in both the development project and the admin profile: components/invoice/src"}]
         (m203/warnings profiles projects color/none))))
