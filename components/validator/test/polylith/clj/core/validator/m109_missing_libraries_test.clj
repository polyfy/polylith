(ns polylith.clj.core.validator.m109-missing-libraries-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.validator.m109-missing-libraries :as m109])
  (:refer-clojure :exclude [bases]))

(def projects [{:name "development"
                :component-names {:src ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]}
                :base-names {:src ["rest-api"]}
                :lib-deps {:src {"clj-jwt" {:mvn/version "0.1.1"}}}
                :profile {:lib-deps {"clj-time" {:mvn/version "0.14.2"}}}}
               {:name "realworld-backend"
                :component-names {:src ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]}
                :base-names ["rest-api"]
                :lib-deps {:src {"clj-jwt" {:mvn/version "0.1.1"}
                                 "com.taoensso/timbre" {:mvn/version "4.10.0"}
                                 "compojure/compojure" {:mvn/version "1.6.0"}
                                 "spec-tools" {:mvn/version "1.0"}
                                 "honeysql" {:mvn/version "0.7.0"}}}
                :profile {:lib-deps {}}}])

(def components [{:name "article"
                  :lib-deps {:src {"clj-time" {:mvn/version "0.14.2"}
                                   "honeysql" {:mvn/version "0.7.0"}}}}
                 {:name "comment"
                  :lib-deps {:src {"clj-time" {:mvn/version "0.14.2"}
                                   "honeysql" {:mvn/version "0.7.0"}}}}
                 {:name "database"
                  :lib-deps {:src {"honeysql" {:mvn/version "0.7.0"}}}}])

(def bases [{:name "rest-api"
             :lib-deps {:src {"spec-tools" {:mvn/version "1.0"}}}}])

(def settings {:ws-type :toolsdeps1
               :profile-to-settings {"default" {:lib-deps {"clj-time" {:size 0, :type "maven", :version "0.14.2"}}}}
               :active-profiles ["default"]})

(def version1 {:ws {:type :toolsdeps1}})
(def version2 {:ws {:type :toolsdeps2}})

(deftest warnings--missing-libraries-in-a-project--returns-a-warning
  (is (= [{:type "error"
           :code 109
           :project "development"
           :message           "Missing libraries in the development project: honeysql, spec-tools"
           :colorized-message "Missing libraries in the development project: honeysql, spec-tools"}
          {:type "error"
           :code 109
           :project "realworld-backend"
           :message           "Missing libraries in the realworld-backend project: clj-time"
           :colorized-message "Missing libraries in the realworld-backend project: clj-time"}]
         (m109/errors "info" settings version1 projects components bases color/none))))

(deftest warnings--when-other-ws-type-than-toolsdeps1--return-no-warnings
  (is (= nil
         (m109/errors "info" settings version2 projects components bases color/none))))
