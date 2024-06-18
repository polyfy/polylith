(ns polylith.clj.core.workspace.enrich.lib-imports-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.enrich.lib-imports :as lib-imports]))

(def interface-names #{"article" "comment" "database" "log" "profile" "spec" "tag" "user"})
(def base-names #{"mybase" "thatbase"})

(def mybase {:name "mybase"
             :type "base"
             :namespaces {:src [{:name "mybase/core.clj"
                                 :imports ["clojure.realworld.profile.interface"
                                           "clojure.realworld.spec.interface"
                                           "clojure.realworld.thatbase.api"
                                           "spec-tools.data-spec"]}
                                {:name "mybase/interface.clj"
                                 :imports ["clojure.realworld.article.core"
                                           "clojure.realworld.article.spec"]}
                                {:name "mybase/store.clj"
                                 :imports ["clojure.java.jdbc"
                                           "clojure.realworld.database.interface"
                                           "clojure.string"
                                           "honeysql.core"]}
                                {:name "mybase/core.clj"
                                 :imports ["clj-time.coerce"
                                           "clj-time.core"
                                           "clojure.realworld.article.store"
                                           "clojure.realworld.profile.interface"
                                           "slugger.core"]}]}})

(deftest lib-imports-src--given-a-component-with-interface-and-library-imports--return-all-imports-except-components
  (is (= {:src ["clj-time.coerce"
                "clj-time.core"
                "clojure.java.jdbc"
                "clojure.string"
                "honeysql.core"
                "slugger.core"
                "spec-tools.data-spec"]}
         (lib-imports/lib-imports "clojure.realworld." interface-names base-names mybase))))
