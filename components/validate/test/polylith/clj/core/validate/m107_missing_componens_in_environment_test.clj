(ns polylith.clj.core.validate.m107-missing-componens-in-environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interfc.color :as color]
            [polylith.clj.core.validate.m107-missing-componens-in-environment :as m107])
  (:refer-clojure :exclude [bases]))

(def components [{:name "article"
                  :type "component"
                  :lines-of-code-src 332
                  :lines-of-code-test 544
                  :interface {:name "article"}
                  :interface-deps ["database" "profile" "spec"]}
                 {:name "build-tools"
                  :type "component"
                  :interface {:name "build-tools", :definitions []}
                  :interface-deps []}
                 {:name "comment"
                  :type "component"
                  :interface {:name "comment"}
                  :interface-deps ["article" "database" "profile" "spec"]}
                 {:name "database"
                  :type "component"
                  :interface {:name "database"}
                  :interface-deps ["log"]}
                 {:name "log"
                  :type "component"
                  :interface {:name "log"}
                  :interface-deps []}
                 {:name "profile"
                  :type "component"
                  :interface {:name "profile"}
                  :interface-deps ["database" "spec" "user"]}
                 {:name "spec"
                  :type "component"
                  :interface {:name "spec"}
                  :interface-deps []}
                 {:name "tag"
                  :type "component"
                  :interface {:name "tag"}
                  :interface-deps ["database"]}
                 {:name "user"
                  :type "component"
                  :interface {:name "user"}
                  :interface-deps ["database" "spec"]}])

(def polylith-bases [{:name "build-tools"
                      :type "base"
                      :interface-deps []}
                     {:name "helpers"
                      :type "base"
                      :interface-deps ["build-tools"]}
                     {:name "rest-api"
                      :type "base"
                      :interface-deps ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]}])

(def environments [{:name "development"
                    :group "development"
                    :test? false
                    :type "environment"
                    :component-names ["article" "user"]
                    :base-names []
                    :deps {}}
                   {:name "development-test"
                    :group "development"
                    :test? true
                    :type "environment"
                    :component-names ["article" "user"]
                    :base-names []
                    :deps {}}
                   {:name "realworld-backend"
                    :group "realworld-backend"
                    :test? false
                    :type "environment"
                    :component-names ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]
                    :base-names ["rest-api"]}
                   {:name "realworld-backend-test"
                    :group "realworld-backend"
                    :test? true
                    :type "environment"
                    :component-names ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]
                    :base-names ["rest-api"]}
                   {:name "helpers"
                    :group "helpers"
                    :test? false
                    :type "environment"
                    :component-names ["build-tools"]
                    :base-names ["helpers"]}
                   {:name "helpers-test"
                    :group "helpers"
                    :test? true
                    :type "environment"
                    :component-names ["build-tools"]
                    :base-names ["helpers"]}])

(deftest errors--environments-with-missing-components--should-return-errors
  (is (= [{:type "error"
           :code 107
           :environment "development"
           :interfaces #{"database"
                         "log"
                         "profile"
                         "spec"}
           :colorized-message "Missing components in the development environment for these interfaces: database, log, profile, spec",
           :message           "Missing components in the development environment for these interfaces: database, log, profile, spec"}]
         (m107/errors components polylith-bases environments color/none))))
