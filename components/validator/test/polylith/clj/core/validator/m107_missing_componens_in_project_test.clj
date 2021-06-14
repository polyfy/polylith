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

(def components [{:name "file"
                  :interface {:name "file"}}
                 {:name "util"
                  :interface {:name "util"}}
                 {:name "user-config"
                  :interface {:name "user-config"}}])

(deftest errors--when-no-active-profiles--ignore-error
  (is (= nil
         (m107/errors "info" settings projects components color/none))))

(deftest errors--when-projects-with-missing-components--return-error
  (is (= [{:code              107
           :colorized-message "Missing components in the poly-migrator project for these interfaces: user-config, util"
           :interfaces        ["user-config" "util"]
           :message           "Missing components in the poly-migrator project for these interfaces: user-config, util"
           :project           "poly-migrator"
           :type              "error"}]
         (m107/errors "info" (assoc settings :active-profiles #{"default"}) projects components color/none))))
