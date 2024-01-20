(ns polylith.clj.core.validator.m107-missing-componens-in-project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m107-missing-bricks-in-project :as m107])
  (:refer-clojure :exclude [bases]))

(def profiles [{:name "default"
                :type "profile"
                :paths []
                :lib-deps {}}
               {:name "admin"
                :type "profile"
                :paths []
                :lib-deps {"zprint" #:mvn{:version "0.4.15"}}}])

(def bases [{:name "worker"}])

(def settings {})

(def projects [{:name "poly-migrator"
                :deps {"common" {:src {:direct ["file"]
                                       :missing-ifc-and-bases {:direct ["user-config"]
                                                               :indirect ["util"]}
                                       :indirect []}}
                       "lib-dep" {:src {:direct ["common" "util"]
                                        :indirect ["file"]}}}}])

(deftest errors--when-no-active-profiles--ignore-error
  (is (= nil
         (m107/errors "info" settings profiles bases projects color/none))))

(deftest errors--when-projects-with-missing-components--return-error
  (is (= [{:bases             []
           :code              107
           :colorized-message "Missing components in the poly-migrator project for these interfaces: user-config, util"
           :interfaces        ["user-config"
                               "util"]
           :message           "Missing components in the poly-migrator project for these interfaces: user-config, util"
           :project           "poly-migrator"
           :type              "error"}]

         (m107/errors "info" (assoc settings :active-profiles #{"default"}) profiles bases projects color/none))))

(def projects2 [{:name "service",
                 :alias "service"
                 :type "project",
                 :test {}
                 :deps {"user" {:src {}, :test {:missing-ifc-and-bases {:direct ["test-helper"], :indirect []}}}}}])

(deftest errors--when-projects-with-missing-components-in-test-context--return-error
  (is (= [{:bases             []
           :code              107
           :colorized-message "Missing components in the service project, for the test context, for these interfaces: test-helper"
           :interfaces        ["test-helper"]
           :message           "Missing components in the service project, for the test context, for these interfaces: test-helper"
           :project           "service"
           :type              "error"}]
         (m107/errors "check" settings profiles bases projects2 color/none))))
