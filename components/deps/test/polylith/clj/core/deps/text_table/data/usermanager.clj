(ns polylith.clj.core.deps.text-table.data.usermanager
  (:refer-clojure :exclude [bases])
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.interface :as common]))

(def interfaces [{:name "app-state",
                  :type "interface",
                  :definitions [{:name "create", :type "function", :parameters [{:name "config"}]}],
                  :implementing-components ["app-state"]}
                 {:name "database",
                  :type "interface",
                  :definitions [{:name "auto-increment-key", :type "function", :parameters [{:name "db-type"}]}
                                {:name "create", :type "function", :parameters [{:name "db-spec"} {:name "init-fn"}]}],
                  :implementing-components ["database"]}
                 {:name "department",
                  :type "interface",
                  :definitions [{:name "get-all", :type "function", :parameters [{:name "db"}]}
                                {:name "get-by-id", :type "function", :parameters [{:name "db"} {:name "id"}]}],
                  :implementing-components ["department"]}
                 {:name "schema",
                  :type "interface",
                  :definitions [{:name "setup-database", :type "function", :parameters [{:name "&"} {:name "[db-spec]"}]}
                                {:name "create+populate",
                                 :type "function",
                                 :parameters [{:name "db"} {:name "db-type"}],
                                 :sub-ns "addressbook"}
                                {:name "create+populate",
                                 :type "function",
                                 :parameters [{:name "db"} {:name "db-type"}],
                                 :sub-ns "department"}],
                  :implementing-components ["schema"]}
                 {:name "schema-fixture",
                  :type "interface",
                  :definitions [{:name "test-db", :type "function", :parameters []}
                                {:name "with-test-db", :type "function", :parameters [{:name "t"}]}],
                  :implementing-components ["schema-fixture"]}
                 {:name "user",
                  :type "interface",
                  :definitions [{:name "delete-by-id", :type "function", :parameters [{:name "db"} {:name "id"}]}
                                {:name "get-all", :type "function", :parameters [{:name "db"}]}
                                {:name "get-by-id", :type "function", :parameters [{:name "db"} {:name "id"}]}
                                {:name "save", :type "function", :parameters [{:name "db"} {:name "user"}]}],
                  :implementing-components ["user"]}
                 {:name "web-server",
                  :type "interface",
                  :definitions [{:name "create", :type "function", :parameters [{:name "handler-fn"} {:name "port"}]}],
                  :implementing-components ["web-server"]}])

(def components [{:interface {:name "app-state",
                              :definitions [{:name "create", :type "function", :parameters [{:name "config"}]}]},
                  :interface-deps {}
                  :lines-of-code {:src 44, :test 6}
                  :name "app-state",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.app-state.interface",
                                      :file-path "../usermanager-example/components/app-state/src/usermanager/app_state/interface.clj",
                                      :imports ["usermanager.app-state.core"]}
                                     {:name "core",
                                      :namespace "usermanager.app-state.core",
                                      :file-path "../usermanager-example/components/app-state/src/usermanager/app_state/core.clj",
                                      :imports ["com.stuartsierra.component"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.app-state.interface-test",
                                       :file-path "../usermanager-example/components/app-state/test/usermanager/app_state/interface_test.clj",
                                       :imports ["clojure.test" "usermanager.app-state.interface"]}]}
                  :lib-deps {:src {"com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006}}
                             :test {}}
                  :lib-imports {:src ["com.stuartsierra.component"]
                                :test ["clojure.test"]}}
                 {:interface {:name "database",
                              :definitions [{:name "auto-increment-key", :type "function", :parameters [{:name "db-type"}]}
                                            {:name "create",
                                             :type "function",
                                             :parameters [{:name "db-spec"} {:name "init-fn"}]}]},
                  :interface-deps {}
                  :lines-of-code {:srd 71, :test 6}
                  :name "database",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.database.interface",
                                      :file-path "../usermanager-example/components/database/src/usermanager/database/interface.clj",
                                      :imports ["usermanager.database.core"]}
                                     {:name "core",
                                      :namespace "usermanager.database.core",
                                      :file-path "../usermanager-example/components/database/src/usermanager/database/core.clj",
                                      :imports ["com.stuartsierra.component" "next.jdbc"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.database.interface-test",
                                       :file-path "../usermanager-example/components/database/test/usermanager/database/interface_test.clj",
                                       :imports ["clojure.test" "usermanager.database.interface"]}]}
                  :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                   "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006}}}
                  :lib-imports {:src ["com.stuartsierra.component" "next.jdbc"]
                                :test ["clojure.test"]}}
                 {:interface {:name "department",
                              :definitions [{:name "get-all", :type "function", :parameters [{:name "db"}]}
                                            {:name "get-by-id", :type "function", :parameters [{:name "db"} {:name "id"}]}]},
                  :interface-deps {:test ["schema-fixture"]}
                  :lines-of-code {:src 26, :test 13}
                  :name "department",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.department.interface",
                                      :file-path "../usermanager-example/components/department/src/usermanager/department/interface.clj",
                                      :imports ["usermanager.department.core"]}
                                     {:name "core",
                                      :namespace "usermanager.department.core",
                                      :file-path "../usermanager-example/components/department/src/usermanager/department/core.clj",
                                      :imports ["next.jdbc.sql"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.department.interface-test",
                                       :file-path "../usermanager-example/components/department/test/usermanager/department/interface_test.clj",
                                       :imports ["clojure.test"
                                                 "usermanager.department.interface"
                                                 "usermanager.schema-fixture.interface"]}]}
                  :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942}}}
                  :lib-imports {:src ["next.jdbc.sql"]
                                :test ["clojure.test"]}}
                 {:interface {:name "schema",
                              :definitions [{:name "setup-database",
                                             :type "function",
                                             :parameters [{:name "&"} {:name "[db-spec]"}]}
                                            {:name "create+populate",
                                             :type "function",
                                             :parameters [{:name "db"} {:name "db-type"}],
                                             :sub-ns "addressbook"}
                                            {:name "create+populate",
                                             :type "function",
                                             :parameters [{:name "db"} {:name "db-type"}],
                                             :sub-ns "department"}]},
                  :interface-deps {:src ["database"],}
                  :lines-of-code {:src 122, :test 8}
                  :name "schema",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.schema.interface",
                                      :file-path "../usermanager-example/components/schema/src/usermanager/schema/interface.clj",
                                      :imports ["usermanager.schema.core"]}
                                     {:name "core",
                                      :namespace "usermanager.schema.core",
                                      :file-path "../usermanager-example/components/schema/src/usermanager/schema/core.clj",
                                      :imports ["usermanager.database.interface"
                                                "usermanager.schema.interface.addressbook"
                                                "usermanager.schema.interface.department"]}
                                     {:name "core.department",
                                      :namespace "usermanager.schema.core.department",
                                      :file-path "../usermanager-example/components/schema/src/usermanager/schema/core/department.clj",
                                      :imports ["next.jdbc" "next.jdbc.sql" "usermanager.database.interface"]}
                                     {:name "core.addressbook",
                                      :namespace "usermanager.schema.core.addressbook",
                                      :file-path "../usermanager-example/components/schema/src/usermanager/schema/core/addressbook.clj",
                                      :imports ["next.jdbc" "next.jdbc.sql" "usermanager.database.interface"]}
                                     {:name "interface.department",
                                      :namespace "usermanager.schema.interface.department",
                                      :file-path "../usermanager-example/components/schema/src/usermanager/schema/interface/department.clj",
                                      :imports ["usermanager.schema.core.department"]}
                                     {:name "interface.addressbook",
                                      :namespace "usermanager.schema.interface.addressbook",
                                      :file-path "../usermanager-example/components/schema/src/usermanager/schema/interface/addressbook.clj",
                                      :imports ["usermanager.schema.core.addressbook"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.schema.interface-test",
                                       :file-path "../usermanager-example/components/schema/test/usermanager/schema/interface_test.clj",
                                       :imports ["clojure.test" "usermanager.schema.interface"]}]}
                  :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942}}}
                  :lib-imports {:src ["next.jdbc" "next.jdbc.sql"]
                                :test ["clojure.test"]}}
                 {:interface {:name "schema-fixture",
                              :definitions [{:name "test-db", :type "function", :parameters []}
                                            {:name "with-test-db", :type "function", :parameters [{:name "t"}]}]},
                  :interface-deps {:src ["schema"]}
                  :lines-of-code {:src 45, :test 8}
                  :name "schema-fixture",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.schema-fixture.interface",
                                      :file-path "../usermanager-example/components/schema-fixture/src/usermanager/schema_fixture/interface.clj",
                                      :imports ["usermanager.schema-fixture.core"]}
                                     {:name "core",
                                      :namespace "usermanager.schema-fixture.core",
                                      :file-path "../usermanager-example/components/schema-fixture/src/usermanager/schema_fixture/core.clj",
                                      :imports ["com.stuartsierra.component" "next.jdbc" "usermanager.schema.interface"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.schema-fixture.interface-test",
                                       :file-path "../usermanager-example/components/schema-fixture/test/usermanager/schema_fixture/interface_test.clj",
                                       :imports ["clojure.test" "usermanager.schema-fixture.interface"]}]}
                  :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                   "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006}}
                             :test {"com.h2database/h2" {:version "1.4.200", :type "maven", :size 2303679}}}
                  :lib-imports {:src ["com.stuartsierra.component" "next.jdbc"]
                                :test ["clojure.test"]}}
                 {:interface {:name "user",
                              :definitions [{:name "delete-by-id",
                                             :type "function",
                                             :parameters [{:name "db"} {:name "id"}]}
                                            {:name "get-all", :type "function", :parameters [{:name "db"}]}
                                            {:name "get-by-id", :type "function", :parameters [{:name "db"} {:name "id"}]}
                                            {:name "save", :type "function", :parameters [{:name "db"} {:name "user"}]}]},
                  :interface-deps {:src ["schema"]
                                   :test ["schema-fixture"]}
                  :lines-of-code {:src 73, :test 35}
                  :name "user",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.user.interface",
                                      :file-path "../usermanager-example/components/user/src/usermanager/user/interface.clj",
                                      :imports ["usermanager.user.core"]}
                                     {:name "core",
                                      :namespace "usermanager.user.core",
                                      :file-path "../usermanager-example/components/user/src/usermanager/user/core.clj",
                                      :imports ["next.jdbc.sql" "usermanager.schema.interface"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.user.interface-test",
                                       :file-path "../usermanager-example/components/user/test/usermanager/user/interface_test.clj",
                                       :imports ["clojure.test"
                                                 "usermanager.schema-fixture.interface"
                                                 "usermanager.user.interface"]}]}
                  :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942}}}
                  :lib-imports {:src ["next.jdbc.sql"]
                                :test ["clojure.test"]}}
                 {:interface {:name "web-server",
                              :definitions [{:name "create",
                                             :type "function",
                                             :parameters [{:name "handler-fn"} {:name "port"}]}]},
                  :interface-deps {}
                  :lines-of-code {:src 66, :test 6}
                  :name "web-server",
                  :type "component",
                  :namespaces {:src [{:name "interface",
                                      :namespace "usermanager.web-server.interface",
                                      :file-path "../usermanager-example/components/web-server/src/usermanager/web_server/interface.clj",
                                      :imports ["usermanager.web-server.core"]}
                                     {:name "core",
                                      :namespace "usermanager.web-server.core",
                                      :file-path "../usermanager-example/components/web-server/src/usermanager/web_server/core.clj",
                                      :imports ["com.stuartsierra.component" "ring.adapter.jetty"]}]
                               :test [{:name "interface-test",
                                       :namespace "usermanager.web-server.interface-test",
                                       :file-path "../usermanager-example/components/web-server/test/usermanager/web_server/interface_test.clj",
                                       :imports ["clojure.test" "usermanager.web-server.interface"]}]}
                  :lib-deps {:src {"com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                   "ring/ring" {:version "1.9.2", :type "maven", :size 4586}}}
                  :lib-imports {:src ["com.stuartsierra.component" "ring.adapter.jetty"]
                                :test ["clojure.test"]}}])

(def bases [{:interface-deps {:src ["app-state" "department" "schema" "user" "web-server"],}
             :lines-of-code {:src 264, :test 10}
             :name "web",
             :type "base",
             :namespaces {:src [{:name "controllers.user",
                                 :namespace "usermanager.web.controllers.user",
                                 :file-path "../usermanager-example/bases/web/src/usermanager/web/controllers/user.clj",
                                 :imports ["ring.util.response"
                                           "selmer.parser"
                                           "usermanager.department.interface"
                                           "usermanager.user.interface"]}
                                {:name "main",
                                 :namespace "usermanager.web.main",
                                 :file-path "../usermanager-example/bases/web/src/usermanager/web/main.clj",
                                 :imports ["com.stuartsierra.component"
                                           "compojure.coercions"
                                           "compojure.core"
                                           "compojure.route"
                                           "ring.middleware.defaults"
                                           "ring.util.response"
                                           "usermanager.app-state.interface"
                                           "usermanager.schema.interface"
                                           "usermanager.web-server.interface"
                                           "usermanager.web.controllers.user"]}]
                          :test [{:name "controllers.user-test",
                                  :namespace "usermanager.web.controllers.user-test",
                                  :file-path "../usermanager-example/bases/web/test/usermanager/web/controllers/user_test.clj",
                                  :imports ["clojure.test" "usermanager.web.controllers.user"]}
                                 {:name "main-test",
                                  :namespace "usermanager.web.main-test",
                                  :file-path "../usermanager-example/bases/web/test/usermanager/web/main_test.clj",
                                  :imports ["clojure.test" "usermanager.web.main"]}]}
             :lib-deps {:src {"com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                              "compojure/compojure" {:version "1.6.2", :type "maven", :size 15172},
                              "ring/ring" {:version "1.9.2", :type "maven", :size 4586},
                              "ring/ring-defaults" {:version "0.3.2", :type "maven", :size 7944},
                              "selmer/selmer" {:version "1.12.33", :type "maven", :size 61829}}}
             :lib-imports {:src ["com.stuartsierra.component"
                                 "compojure.coercions"
                                 "compojure.core"
                                 "compojure.route"
                                 "ring.middleware.defaults"
                                 "ring.util.response"
                                 "selmer.parser"
                                 "usermanager.web.controllers.user"]
                           :test ["clojure.test" "usermanager.web.controllers.user" "usermanager.web.main"]}}])

(def projects [{:base-names {:src ["web"], :test ["web"]}
                :component-names {:src ["app-state" "database" "department" "schema" "user" "web-server"]
                                  :test ["app-state" "database" "department" "schema" "schema-fixture" "user" "web-server"]}
                :lib-imports {:src ["com.stuartsierra.component"
                                    "compojure.coercions"
                                    "compojure.core"
                                    "compojure.route"
                                    "next.jdbc"
                                    "next.jdbc.sql"
                                    "ring.adapter.jetty"
                                    "ring.middleware.defaults"
                                    "ring.util.response"
                                    "selmer.parser"
                                    "usermanager.web.controllers.user"]
                              :test ["clojure.test" "usermanager.web.controllers.user" "usermanager.web.main"]}
                :is-dev false,
                :lines-of-code {:src 0, :test 0 :total {:src 666, :test 92}}
                :name "usermanager",
                :is-run-tests true,
                :type "project",
                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                              "clojars" {:url "https://repo.clojars.org/"}},
                :alias "um",
                :project-dir "../usermanager-example/projects/usermanager",
                :namespaces {}
                :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                 "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                 "compojure/compojure" {:version "1.6.2", :type "maven", :size 15172},
                                 "org.clojure/clojure" {:version "1.10.3", :type "maven", :size 3914649},
                                 "org.xerial/sqlite-jdbc" {:version "3.34.0", :type "maven", :size 7296329},
                                 "ring/ring" {:version "1.9.2", :type "maven", :size 4586},
                                 "ring/ring-defaults" {:version "0.3.2", :type "maven", :size 7944},
                                 "selmer/selmer" {:version "1.12.33", :type "maven", :size 61829}}
                           :test {"org.clojure/clojure" {:version "1.10.3", :type "maven", :size 3914649},
                                  "org.xerial/sqlite-jdbc" {:version "3.34.0", :type "maven", :size 7296329},
                                  "com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                  "com.h2database/h2" {:version "1.4.200", :type "maven", :size 2303679},
                                  "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                  "org.clojure/test.check" {:version "1.1.0", :type "maven", :size 39487}}}
                :config-filename "../usermanager-example/projects/usermanager/deps.edn",
                :paths {:src ["bases/web/resources"
                              "bases/web/src"
                              "components/app-state/resources"
                              "components/app-state/src"
                              "components/database/resources"
                              "components/database/src"
                              "components/department/resources"
                              "components/department/src"
                              "components/schema/resources"
                              "components/schema/src"
                              "components/user/resources"
                              "components/user/src"
                              "components/web-server/resources"
                              "components/web-server/src"
                              "projects/usermanager/resources"
                              "projects/usermanager/src"]
                        :test ["bases/web/test"
                               "components/app-state/test"
                               "components/database/test"
                               "components/department/test"
                               "components/schema-fixture/resources"
                               "components/schema-fixture/src"
                               "components/schema-fixture/test"
                               "components/schema/test"
                               "components/user/test"
                               "components/web-server/test"
                               "projects/usermanager/resources"
                               "projects/usermanager/src"
                               "projects/usermanager/test"]}
                :deps {"web" {:src {:direct ["app-state" "department" "schema" "user" "web-server"],
                                    :indirect ["database"],
                                    :missing-ifc {:direct [], :indirect []}},
                              :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "app-state" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                    :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "database" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                   :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "department" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                     :test {:direct ["schema-fixture"],
                                            :indirect [],
                                            :missing-ifc {:direct [], :indirect []}}},
                       "schema" {:src {:direct ["database"], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                 :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "schema-fixture" {:src {:direct ["schema"],
                                               :indirect ["database"],
                                               :missing-ifc {:direct [], :indirect []}},
                                         :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "user" {:src {:direct ["schema"], :indirect ["database"], :missing-ifc {:direct [], :indirect []}},
                               :test {:direct ["schema-fixture"], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "web-server" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                     :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}}}}
               {:base-names {:src ["web"], :test ["web"]}
                :lib-imports {:src ["com.stuartsierra.component"
                                    "compojure.coercions"
                                    "compojure.core"
                                    "compojure.route"
                                    "next.jdbc"
                                    "next.jdbc.sql"
                                    "ring.adapter.jetty"
                                    "ring.middleware.defaults"
                                    "ring.util.response"
                                    "selmer.parser"
                                    "usermanager.web.controllers.user"]
                              :test ["clojure.test" "usermanager.web.controllers.user" "usermanager.web.main"]}
                :is-dev true,
                :lines-of-code {:src 13, :test 0 :total {:src 666, :test 92}}
                :name "development",
                :is-run-tests false,
                :type "project",
                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                              "clojars" {:url "https://repo.clojars.org/"}},
                :alias "dev",
                :project-dir "../usermanager-example/development",
                :unmerged {:paths {:src ["bases/web/resources"
                                         "bases/web/src"
                                         "components/app-state/resources"
                                         "components/app-state/src"
                                         "components/database/resources"
                                         "components/database/src"
                                         "components/department/resources"
                                         "components/department/src"
                                         "components/schema/resources"
                                         "components/schema/src"
                                         "components/user/resources"
                                         "components/user/src"
                                         "components/web-server/resources"
                                         "components/web-server/src"
                                         "development/resources"
                                         "development/src"]
                                   :test ["bases/web/test"
                                          "components/app-state/test"
                                          "components/database/test"
                                          "components/department/test"
                                          "components/schema-fixture/test"
                                          "components/schema/test"
                                          "components/user/test"
                                          "components/web-server/test"
                                          "development/resources"
                                          "development/src"
                                          "development/test"
                                          "projects/usermanager/test"]}
                           :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                            "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                            "compojure/compojure" {:version "1.6.2", :type "maven", :size 15172},
                                            "org.clojure/clojure" {:version "1.10.3", :type "maven", :size 3914649},
                                            "org.xerial/sqlite-jdbc" {:version "3.34.0", :type "maven", :size 7296329},
                                            "ring/ring" {:version "1.9.2", :type "maven", :size 4586},
                                            "ring/ring-defaults" {:version "0.3.2", :type "maven", :size 7944},
                                            "selmer/selmer" {:version "1.12.33", :type "maven", :size 61829}}
                                      :test {"org.clojure/test.check" {:version "1.1.0", :type "maven", :size 39487},
                                             "ring/ring" {:version "1.9.2", :type "maven", :size 4586},
                                             "ring/ring-defaults" {:version "0.3.2", :type "maven", :size 7944},
                                             "com.h2database/h2" {:version "1.4.200", :type "maven", :size 2303679},
                                             "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                             "com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                             "selmer/selmer" {:version "1.12.33", :type "maven", :size 61829},
                                             "compojure/compojure" {:version "1.6.2", :type "maven", :size 15172},
                                             "org.xerial/sqlite-jdbc" {:version "3.34.0", :type "maven", :size 7296329},
                                             "org.clojure/clojure" {:version "1.10.3", :type "maven", :size 3914649}}}}
                :namespaces {:src [{:name "dev",
                                    :namespace "dev",
                                    :file-path "../usermanager-example/development/src/dev.clj",
                                    :imports []}],}
                :lib-deps {:src {"com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                 "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                 "compojure/compojure" {:version "1.6.2", :type "maven", :size 15172},
                                 "org.clojure/clojure" {:version "1.10.3", :type "maven", :size 3914649},
                                 "org.xerial/sqlite-jdbc" {:version "3.34.0", :type "maven", :size 7296329},
                                 "ring/ring" {:version "1.9.2", :type "maven", :size 4586},
                                 "ring/ring-defaults" {:version "0.3.2", :type "maven", :size 7944},
                                 "selmer/selmer" {:version "1.12.33", :type "maven", :size 61829}}
                           :test {"org.clojure/test.check" {:version "1.1.0", :type "maven", :size 39487},
                                  "ring/ring" {:version "1.9.2", :type "maven", :size 4586},
                                  "ring/ring-defaults" {:version "0.3.2", :type "maven", :size 7944},
                                  "com.h2database/h2" {:version "1.4.200", :type "maven", :size 2303679},
                                  "com.stuartsierra/component" {:version "1.0.0", :type "maven", :size 19006},
                                  "com.github.seancorfield/next.jdbc" {:version "1.2.689", :type "maven", :size 44942},
                                  "selmer/selmer" {:version "1.12.33", :type "maven", :size 61829},
                                  "compojure/compojure" {:version "1.6.2", :type "maven", :size 15172},
                                  "org.xerial/sqlite-jdbc" {:version "3.34.0", :type "maven", :size 7296329},
                                  "org.clojure/clojure" {:version "1.10.3", :type "maven", :size 3914649}}}
                :test-component-names ["app-state" "database" "department" "schema" "schema-fixture" "user" "web-server"],
                :config-filename "../usermanager-example/deps.edn",
                :component-names ["app-state" "database" "department" "schema" "user" "web-server"],
                :paths {:src ["bases/web/resources"
                              "bases/web/src"
                              "components/app-state/resources"
                              "components/app-state/src"
                              "components/database/resources"
                              "components/database/src"
                              "components/department/resources"
                              "components/department/src"
                              "components/schema/resources"
                              "components/schema/src"
                              "components/user/resources"
                              "components/user/src"
                              "components/web-server/resources"
                              "components/web-server/src"
                              "development/resources"
                              "development/src"]
                        :test ["bases/web/test"
                               "components/app-state/test"
                               "components/database/test"
                               "components/department/test"
                               "components/schema-fixture/test"
                               "components/schema/test"
                               "components/user/test"
                               "components/web-server/test"
                               "development/resources"
                               "development/src"
                               "development/test"
                               "projects/usermanager/test"]}
                :deps {"web" {:src {:direct ["app-state" "department" "schema" "user" "web-server"],
                                    :indirect ["database"],
                                    :missing-ifc {:direct [], :indirect []}},
                              :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "app-state" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                    :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "database" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                   :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "department" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                     :test {:direct ["schema-fixture"],
                                            :indirect [],
                                            :missing-ifc {:direct [], :indirect []}}},
                       "schema" {:src {:direct ["database"], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                 :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "schema-fixture" {:src {:direct ["schema"],
                                               :indirect ["database"],
                                               :missing-ifc {:direct [], :indirect []}},
                                         :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "user" {:src {:direct ["schema"], :indirect ["database"], :missing-ifc {:direct [], :indirect []}},
                               :test {:direct ["schema-fixture"], :indirect [], :missing-ifc {:direct [], :indirect []}}},
                       "web-server" {:src {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}},
                                     :test {:direct [], :indirect [], :missing-ifc {:direct [], :indirect []}}}}}])

(def paths {:existing ["bases/web/resources"
                       "bases/web/src"
                       "bases/web/test"
                       "components/app-state/resources"
                       "components/app-state/src"
                       "components/app-state/test"
                       "components/database/resources"
                       "components/database/src"
                       "components/database/test"
                       "components/department/resources"
                       "components/department/src"
                       "components/department/test"
                       "components/schema-fixture/resources"
                       "components/schema-fixture/src"
                       "components/schema-fixture/test"
                       "components/schema/resources"
                       "components/schema/src"
                       "components/schema/test"
                       "components/user/resources"
                       "components/user/src"
                       "components/user/test"
                       "components/web-server/resources"
                       "components/web-server/src"
                       "components/web-server/test"
                       "development/resources"
                       "development/src"
                       "development/test"
                       "projects/usermanager/resources"
                       "projects/usermanager/src"
                       "projects/usermanager/test"],
            :missing [],
            :on-disk ["bases/web/resources"
                      "bases/web/src"
                      "bases/web/test"
                      "components/app-state/resources"
                      "components/app-state/src"
                      "components/app-state/test"
                      "components/database/resources"
                      "components/database/src"
                      "components/database/test"
                      "components/department/resources"
                      "components/department/src"
                      "components/department/test"
                      "components/schema-fixture/resources"
                      "components/schema-fixture/src"
                      "components/schema-fixture/test"
                      "components/schema/resources"
                      "components/schema/src"
                      "components/schema/test"
                      "components/user/resources"
                      "components/user/src"
                      "components/user/test"
                      "components/web-server/resources"
                      "components/web-server/src"
                      "components/web-server/test"
                      "projects/usermanager/resources"
                      "projects/usermanager/src"
                      "projects/usermanager/test"]})

(def settings {:version "0.2.0-alpha10.issue66.03",
               :vcs {:name "git"
                     :auto-add false}
               :top-namespace "usermanager",
               :interface-ns "interface",
               :default-profile-name "default",
               :active-profiles #{},
               :tag-patterns {:stable "stable-*"
                              :release "v[0-9]*"}
               :color-mode "none",
               :compact-views #{},
               :user-config-filename "/Users/joakimtengstrand/.config/polylith/config.edn",
               :empty-character ".",
               :thousand-separator ",",
               :profile-to-settings {},
               :projects {"development" {:alias "dev"}, "usermanager" {:alias "um"}},
               :user-home "/Users/joakimtengstrand",
               :m2-dir "/Users/joakimtengstrand/.m2"})
(def changes {:since "stable",
              :since-sha "1e06c52c982e4619f91e74de08dd48251b61b545",
              :since-tag "stable-sean",
              :git-diff-command "git diff 1e06c52c982e4619f91e74de08dd48251b61b545 --name-only",
              :changed-components ["schema" "user"],
              :changed-bases ["web"],
              :changed-projects [],
              :changed-or-affected-projects ["development" "usermanager"],
              :project-to-indirect-changes {"usermanager" ["schema-fixture"], "development" ["schema-fixture"]},
              :project-to-indirect-changes-test {"usermanager" ["department"], "development" ["department"]},
              :project-to-bricks-to-test {"usermanager" ["department" "schema" "user" "web"], "development" []},
              :project-to-projects-to-test {"usermanager" [], "development" []},
              :changed-files ["README.md"
                              "bases/web/src/usermanager/web/main.clj"
                              "components/schema/src/usermanager/schema/core.clj"
                              "components/user/src/usermanager/user/core.clj"
                              "deps.edn"
                              "images/workspace.png"]})

(def workspace {:projects projects,
                :ws-dir "../usermanager-example",
                :name "usermanager-example",
                :settings settings,
                :paths paths,
                :messages [],
                :components components,
                :bases bases
                :changes changes})

(def usermanager (common/find-project "usermanager" projects))
(def department (common/find-component "department" components))
(def schema (common/find-component "schema" components))
(def schema-fixture (common/find-component "schema-fixture" components))

(def workspace2 {:interfaces interfaces,
                 :projects projects,
                 :ws-dir "../usermanager-example",
                 :name "usermanager-example",
                 :settings settings,
                 :paths paths,
                 :components components,
                 :changes changes,
                 :bases bases})
