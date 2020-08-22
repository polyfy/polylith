(ns polylith.clj.core.validate.m204-path-exists-in-both-dev-and-profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validate.m204-path-exists-in-both-dev-and-profile :as m204]))

(def settings {:profile->settings {"default" {:paths ["components/user/src"
                                                      "components/user/test"]}
                                   "admin" {:paths ["components/admin/src"
                                                    "components/admin/test"
                                                    "components/invoice/src"]}}})

(def environments [{:alias "dev",
                    :src-paths ["components/invoice/src"
                                "development/src"]
                    :test-paths []}])

(deftest warnings--path-was-found-in-both-dev-and-a-profile--returns-error-message
  (is (= [{:type "warning"
           :code 204
           :message "The same path exists in both the development environment and the 'admin' profile: components/invoice/src"
           :colorized-message "The same path exists in both the development environment and the 'admin' profile: components/invoice/src"}]
         (m204/warnings settings environments))))
