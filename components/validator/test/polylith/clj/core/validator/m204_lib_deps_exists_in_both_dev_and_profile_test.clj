(ns polylith.clj.core.validator.m204-lib-deps-exists-in-both-dev-and-profile-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m204-lib-deps-exists-in-both-dev-and-profile :as m204]))

(def settings {:profile-to-settings {"default" {:lib-deps {}}
                                     "admin" {:lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"}}}}})

(def projects [{:alias        "dev"
                :unmerged {:lib-deps {:src {"org.clojure/clojure" {:mvn/version "1.10.2"}
                                            "org.clojure/tools.deps"{:mvn/version "0.16.1264"}}}}}])

(deftest warnings--path-was-found-in-both-dev-and-a-profile--returns-error-message
  (is (= [{:type "warning"
           :code 204
           :message "The same library dependency exists in both the development project and the admin profile: org.clojure/clojure"
           :colorized-message "The same library dependency exists in both the development project and the admin profile: org.clojure/clojure"}]
         (m204/warnings settings projects color/none))))
