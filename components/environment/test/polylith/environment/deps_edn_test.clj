(ns polylith.environment.deps-edn-test
  (:require [clojure.test :refer :all]
            [polylith.environment.deps-edn :as env]))

(def config '{:paths ["shared/src"]

              :polylith {:top-namespace "clojure.realworld"
                         :env-prefix "env"}

              :ring {:init clojure.realworld.rest-api.api/init
                     :destroy clojure.realworld.rest-api.api/destroy
                     :handler clojure.realworld.rest-api.api/app
                     :port 6003}

              :deps {clj-time {:mvn/version "0.14.2"}
                     org.clojure/clojure {:mvn/version "1.10.0"}
                     metosin/spec-tools {:mvn/version "0.6.1"}}

              :aliases {:dev {:extra-deps {tengstrand/polylith {:git/url "https://github.com/rtengstrand/polylith.git"
                                                                :sha "89a91b1c519b338eb5a15c90cb97559c09484e89"}}}

                        :env/realworld-backend {:extra-paths ["bases/build-tools/src"
                                                              "bases/rest-api/resources"
                                                              "bases/rest-api/src"
                                                              "components/article/src"
                                                              "components/article/resources"
                                                              "components/comment/src"
                                                              "components/comment/resources"
                                                              "components/database/src"
                                                              "components/database/resources"]

                                                :extra-deps  {clj-jwt                 {:mvn/version "0.1.1"}
                                                              com.taoensso/timbre     {:mvn/version "4.10.0"}
                                                              compojure/compojure     {:mvn/version "1.6.0"}
                                                              crypto-password         {:mvn/version "0.2.0"}}}

                        :env/realworld-backend-test {:extra-paths ["bases/rest-api/test"
                                                                   "include-me/test"
                                                                   "components/article/test"
                                                                   "components/comment/test"
                                                                   "components/database/test"]
                                                     :extra-deps  {org.clojure/test.check {:mvn/version "0.10.0-alpha3"}}}

                        :env/build-tools {:extra-paths ["bases/build-tools/src"]
                                          :extra-deps  {ring-server {:mvn/version "0.5.0"}}}


                        :env/build-tools-test {:extra-paths ["bases/build-tools/test"]
                                               :extra-deps  {org.clojure/test.check {:mvn/version "0.10.0-alpha3"}}}}})

(deftest environments--config-map-with-aliases--returns-environments
  (is (= '[{:name "build-tools"
            :group "build-tools"
            :test? false
            :components []
            :bases [{:name "build-tools", :type "base"}]
            :extra-paths ["shared/src"]
            :dependencies {clj-time #:mvn{:version "0.14.2"}
                           org.clojure/clojure #:mvn{:version "1.10.0"}
                           metosin/spec-tools #:mvn{:version "0.6.1"}
                           ring-server #:mvn{:version "0.5.0"}}}
           {:name "build-tools-test"
            :group "build-tools"
            :test? true
            :components []
            :bases [{:name "build-tools", :type "base"}]
            :extra-paths ["shared/src"]
            :dependencies {clj-time #:mvn{:version "0.14.2"}
                           org.clojure/clojure #:mvn{:version "1.10.0"}
                           metosin/spec-tools #:mvn{:version "0.6.1"}
                           org.clojure/test.check #:mvn{:version "0.10.0-alpha3"}}}
           {:name "realworld-backend"
            :group "realworld-backend"
            :test? false
            :components [{:name "article", :type "component"}
                         {:name "comment", :type "component"}
                         {:name "database", :type "component"}]
            :bases [{:name "build-tools", :type "base"} {:name "rest-api", :type "base"}]
            :extra-paths ["shared/src"]
            :dependencies {clj-time #:mvn{:version "0.14.2"}
                           org.clojure/clojure #:mvn{:version "1.10.0"}
                           metosin/spec-tools #:mvn{:version "0.6.1"}
                           clj-jwt #:mvn{:version "0.1.1"}
                           com.taoensso/timbre #:mvn{:version "4.10.0"}
                           compojure/compojure #:mvn{:version "1.6.0"}
                           crypto-password #:mvn{:version "0.2.0"}}}
           {:name "realworld-backend-test"
            :group "realworld-backend"
            :test? true
            :components [{:name "article", :type "component"}
                         {:name "comment", :type "component"}
                         {:name "database", :type "component"}]
            :bases [{:name "rest-api", :type "base"}]
            :extra-paths ["include-me/test" "shared/src"]
            :dependencies {clj-time #:mvn{:version "0.14.2"}
                           org.clojure/clojure #:mvn{:version "1.10.0"}
                           metosin/spec-tools #:mvn{:version "0.6.1"}
                                     org.clojure/test.check #:mvn{:version "0.10.0-alpha3"}}}]
         (env/environments config))))
