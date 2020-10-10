(ns polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m203-path-exists-in-both-dev-and-profile :as m203]))

(def settings {:profile-to-settings {"default" {:paths ["components/user/src"
                                                        "components/user/test"]}
                                     "admin" {:paths ["components/admin/src"
                                                      "components/admin/test"
                                                      "components/invoice/src"]}}})

(def environments [{:alias "dev"
                    :unmerged {:src-paths ["components/invoice/src"
                                           "development/src"]
                               :test-paths []}}])

(deftest warnings--path-was-found-in-both-dev-and-a-profile--returns-error-message
  (is (= [{:code 203
           :type "warning"
           :message "The same path exists in both the development environment and the admin profile: components/invoice/src"
           :colorized-message "The same path exists in both the development environment and the admin profile: components/invoice/src"}]
         (m203/warnings settings environments color/none))))
