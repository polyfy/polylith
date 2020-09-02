(ns polylith.clj.core.validator.m107-missing-componens-in-environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validator.m107-missing-componens-in-environment :as m107])
  (:refer-clojure :exclude [bases]))

(def settings {:profile->settings {"default" {:paths []
                                              :lib-deps {}}
                                   "admin" {:paths []
                                            :lib-deps {"zprint" #:mvn{:version "0.4.15"}}}}})

(def environments [{:name "migrator"
                    :deps {"common" {:direct ["file" "util"]
                                     :direct-ifc ["user-config"]
                                     :indirect []}
                           "lib-dep" {:direct ["common" "util"]
                                      :indirect ["file"]}}}])

(deftest errors--when-no-active-dev-profiles--ignore-error
  (is (= nil
         (m107/errors settings environments #{} color/none))))

(deftest errors--when-environments-with-missing-components--return-error
  (is (= [{:type "error"
           :code 107
           :environment "migrator"
           :interfaces ["user-config"]
           :colorized-message "Missing components in the migrator environment for these interfaces: user-config"
           :message           "Missing components in the migrator environment for these interfaces: user-config"}]
         (m107/errors settings environments #{"default"} color/none))))
