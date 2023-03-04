(ns polylith.clj.core.validator.m107-missing-componens-in-project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m107-missing-componens-in-project :as m107])
  (:refer-clojure :exclude [bases]))

(def settings {:profile-to-settings {"default" {:paths []
                                                :lib-deps {}}
                                     "admin" {:paths []
                                              :lib-deps {"zprint" #:mvn{:version "0.4.15"}}}}})

(def projects [{:name "poly-migrator"
                :deps {"common" {:src {:direct ["file"]
                                       :missing-ifc {:direct ["user-config"]
                                                     :indirect ["util"]}
                                       :indirect []}}
                       "lib-dep" {:src {:direct ["common" "util"]
                                        :indirect ["file"]}}}}])

(deftest errors--when-no-active-profiles--ignore-error
  (is (= nil
         (m107/errors "info" settings projects color/none))))

(deftest errors--when-projects-with-missing-components--return-error
  (is (= [{:code              107
           :type              "error"
           :message           "Missing components in the poly-migrator project for these interfaces: user-config, util"
           :colorized-message "Missing components in the poly-migrator project for these interfaces: user-config, util"
           :interfaces        ["user-config" "util"]
           :project           "poly-migrator"}]

         (m107/errors "info" (assoc settings :active-profiles #{"default"}) projects color/none))))


(def settings2 {:projects {"development" {:alias "dev"}
                           "service" {:alias "service"
                                      :test {}}}})

(def projects2 [{:name "service",
                 :type "project",
                 :deps {"user" {:src {}, :test {:missing-ifc {:direct ["test-helper"], :indirect []}}}}}])

(deftest errors--when-projects-with-missing-components-in-test-context--return-error
  (is (= [{:type              "error"
           :code              107
           :message           "Missing components in the service project, for the test context, for these interfaces: test-helper"
           :colorized-message "Missing components in the service project, for the test context, for these interfaces: test-helper"
           :interfaces        ["test-helper"]
           :project           "service"}]
         (m107/errors "check" settings2 projects2 color/none))))
