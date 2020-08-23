(ns polylith.clj.core.validator.m202-missing-libraries-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.validator.m202-missing-libraries :as m202])
  (:refer-clojure :exclude [bases]))

(def environments [{:name "development"
                    :component-names ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]
                    :base-names ["rest-api"]
                    :lib-deps {"clj-jwt" #:mvn{:version "0.1.1"}
                               "clj-time" #:mvn{:version "0.14.2"}
                               "com.taoensso/timbre" #:mvn{:version "4.10.0"}
                               "compojure/compojure" #:mvn{:version "1.6.0"}}}
                   {:name "realworld-backend"
                    :component-names ["article" "comment" "database" "log" "profile" "spec" "tag" "user"]
                    :base-names ["rest-api"]
                    :lib-deps {"clj-jwt" #:mvn{:version "0.1.1"}
                               "com.taoensso/timbre" #:mvn{:version "4.10.0"}
                               "compojure/compojure" #:mvn{:version "1.6.0"}}}])

(def components [{:name "article"
                  :namespaces-src [{:name "spec"
                                    :namespace "clojure.realworld.article.spec"
                                    :file-path "../clojure-polylith-realworld-example-app/components/article/src/clojure/realworld/article/spec.clj"
                                    :imports ["clojure.realworld.profile.interface"
                                              "clojure.realworld.spec.interface"
                                              "spec-tools.data-spec"]}
                                   {:name "interface"
                                    :namespace "clojure.realworld.article.interface"
                                    :file-path "../clojure-polylith-realworld-example-app/components/article/src/clojure/realworld/article/interface.clj"
                                    :imports ["clojure.realworld.article.core" "clojure.realworld.article.spec"]}
                                   {:name "core"
                                    :namespace "clojure.realworld.article.core"
                                    :file-path "../clojure-polylith-realworld-example-app/components/article/src/clojure/realworld/article/core.clj"
                                    :imports ["clj-time.coerce"
                                              "slugger.core"]}]
                  :lib-deps ["clj-time" "clojure" "clojure.java.jdbc" "honeysql" "spec-tools"]}
                 {:name "comment"
                  :namespaces-src [{:name "spec"
                                    :namespace "clojure.realworld.comment.spec"
                                    :file-path "../clojure-polylith-realworld-example-app/components/comment/src/clojure/realworld/comment/spec.clj"
                                    :imports ["clojure.realworld.profile.interface"
                                              "clojure.realworld.spec.interface"
                                              "spec-tools.core"
                                              "spec-tools.data-spec"]}
                                   {:name "interface"
                                    :namespace "clojure.realworld.comment.interface"
                                    :file-path "../clojure-polylith-realworld-example-app/components/comment/src/clojure/realworld/comment/interface.clj"
                                    :imports ["clojure.realworld.comment.core" "clojure.realworld.comment.spec"]}
                                   {:name "store"
                                    :namespace "clojure.realworld.comment.store"
                                    :file-path "../clojure-polylith-realworld-example-app/components/comment/src/clojure/realworld/comment/store.clj"
                                    :imports ["clojure.java.jdbc" "clojure.realworld.database.interface" "honeysql.core"]}
                                   {:name "core"
                                    :namespace "clojure.realworld.comment.core"
                                    :file-path "../clojure-polylith-realworld-example-app/components/comment/src/clojure/realworld/comment/core.clj"
                                    :imports ["clj-time.core"
                                              "clojure.realworld.article.interface"
                                              "clojure.realworld.comment.store"
                                              "clojure.realworld.profile.interface"]}]
                  :lib-deps ["clj-time" "clojure.java.jdbc" "honeysql" "spec-tools"]}
                 {:name "database"
                  :namespaces-src [{:name "interface"
                                    :namespace "clojure.realworld.database.interface"
                                    :file-path "../clojure-polylith-realworld-example-app/components/database/src/clojure/realworld/database/interface.clj"
                                    :imports ["clojure.realworld.database.core" "clojure.realworld.database.schema"]}
                                   {:name "core"
                                    :namespace "clojure.realworld.database.core"
                                    :file-path "../clojure-polylith-realworld-example-app/components/database/src/clojure/realworld/database/core.clj"
                                    :imports ["clojure.java.io"]}
                                   {:name "schema"
                                    :namespace "clojure.realworld.database.schema"
                                    :file-path "../clojure-polylith-realworld-example-app/components/database/src/clojure/realworld/database/schema.clj"
                                    :imports ["clojure.java.jdbc" "clojure.realworld.log.interface" "honeysql.core"]}]
                  :lib-deps ["clojure" "clojure.java.jdbc" "honeysql"]}])

(def bases [{:name "rest-api"
             :namespaces-src [{:name "handler"
                               :namespace "clojure.realworld.rest-api.handler"
                               :file-path "../clojure-polylith-realworld-example-app/bases/rest-api/src/clojure/realworld/rest_api/handler.clj"
                               :imports ["clojure.edn"
                                         "clojure.realworld.article.interface"
                                         "clojure.realworld.comment.interface"
                                         "clojure.realworld.profile.interface"
                                         "clojure.realworld.spec.interface"
                                         "clojure.realworld.tag.interface"
                                         "clojure.realworld.user.interface"
                                         "clojure.spec.alpha"]}
                              {:name "middleware",
                               :namespace "clojure.realworld.rest-api.middleware"
                               :file-path "../clojure-polylith-realworld-example-app/bases/rest-api/src/clojure/realworld/rest_api/middleware.clj"
                               :imports ["clojure.realworld.log.interface" "clojure.realworld.user.interface" "clojure.string"]}
                              {:name "api"
                               :namespace "clojure.realworld.rest-api.api"
                               :file-path "../clojure-polylith-realworld-example-app/bases/rest-api/src/clojure/realworld/rest_api/api.clj"
                               :imports ["clojure.realworld.database.interface"]}]
             :lib-bases ["clojure"]}])

(def ns->lib {"clj-jwt" "clj-jwt"
              "clj-time" "clj-time"
              "taoensso.timbre"
              "com.taoensso/timbre"
              "compojure" "compojure/compojure"})

(deftest warnings--missing-libraries-in-an-environment--returns-a-warning
  (is (= [{:type "warning"
           :code 202
           :message "Missing libraries for the realworld-backend environment: clj-time"
           :colorized-message "Missing libraries for the [35mrealworld-backend[0m environment: [37mclj-time[0m"
           :environment "realworld-backend"}])
    (m202/warnings environments components bases ns->lib "dark")))
