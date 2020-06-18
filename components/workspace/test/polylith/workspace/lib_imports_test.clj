(ns polylith.workspace.lib-imports-test
  (:require [clojure.test :refer :all]
            [polylith.workspace.lib-imports :as lib-imports]))

(def component {:name "article"
                :type "component"
                :namespaces [{:name "article/spec.clj"
                              :imports ["clojure.realworld.profile.interface"
                                        "clojure.realworld.spec.interface"
                                        "spec-tools.data-spec"]}
                             {:name "article/interface.clj"
                              :imports ["clojure.realworld.article.core"
                                        "clojure.realworld.article.spec"]}
                             {:name "article/store.clj"
                              :imports ["clojure.java.jdbc"
                                        "clojure.realworld.database.interface"
                                        "clojure.string"
                                        "honeysql.core"]}
                             {:name "article/core.clj"
                              :imports ["clj-time.coerce"
                                        "clj-time.core"
                                        "clojure.realworld.article.store"
                                        "clojure.realworld.profile.interface"
                                        "slugger.core"]}]})

(def interface-names #{"article" "comment" "database" "log" "profile" "spec" "tag" "user"})

(deftest lib-imports--given-a-component-with-interface-and-library-imports--return-all-imports-except-components
  (is (= ["clj-time.coerce"
          "clj-time.core"
          "clojure.java.jdbc"
          "clojure.string"
          "honeysql.core"
          "slugger.core"
          "spec-tools.data-spec"]
         (lib-imports/lib-imports "clojure.realworld." interface-names component))))
