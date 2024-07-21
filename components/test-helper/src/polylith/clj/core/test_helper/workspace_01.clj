(ns polylith.clj.core.test-helper.workspace-01)

(def workspace {:interfaces [{:name "database", :type "interface", :definitions [], :implementing-components ["database"]}
                             {:name "test-helper",
                              :type "interface",
                              :definitions [{:name "do-stuff", :type "function", :arglist []}],
                              :implementing-components ["test-helper"]}
                             {:name "user",
                              :type "interface",
                              :definitions [{:name "value", :type "data"}],
                              :implementing-components ["user"]}],
                :projects [{:base-names {}
                            :lib-imports {:test ["clojure.test"]}
                            :is-dev false,
                            :lines-of-code {:src 0, :test 0, :total {:src 3, :test 9}}
                            :name "service",
                            :type "project",
                            :bricks-to-test []
                            :projects-to-test []
                            :indirect-changes []
                            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                          "clojars" {:url "https://repo.clojars.org/"}},
                            :alias "s",
                            :project-dir "../sandbox/ws02/projects/service",
                            :namespaces {}
                            :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                             "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}}
                                       :test {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566},
                                              "clj-time" {:version "0.15.2", :type "maven"}}}
                            :deps-filename "../sandbox/ws02/projects/service/deps.edn",
                            :deps-test {"user" {:direct [], :direct-ifc [], :indirect []}},
                            :component-names {:src ["user"], :test ["user"]}
                            :paths {:src ["components/user/src"]
                                    :test ["components/user/test"]}
                            :deps {"user" {:direct [], :direct-ifc [], :indirect []}}}
                           {:base-names {},
                            :lib-imports {:test ["clojure.test"]}
                            :is-dev true,
                            :lines-of-code {:src 0, :test 0, :total {:src 9, :test 18}}
                            :name "development",
                            :indirect-changes [],
                            :type "project",
                            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                          "clojars" {:url "https://repo.clojars.org/"}},
                            :alias "dev",
                            :project-dir "../sandbox/ws02/development",
                            :unmerged {:paths {:src ["components/database/src"
                                                     "components/test-helper/src"
                                                     "components/user/src"
                                                     "development/src"]
                                               :test ["components/database/src"
                                                      "components/test-helper/src"
                                                      "components/user/src"
                                                      "development/src"
                                                      "components/database/test"
                                                      "components/user/test"]}
                                       :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                                        "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}}
                                                  :test {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                                         "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}}}}
                            :namespaces {}
                            :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                             "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566},
                                             "clj-commons/fs" {:version "1.6.310", :type "maven", :size 12819}}
                                       :test {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps"{:version "0.16.1264", :type "maven", :size 47566}}}
                            :deps-filename "../sandbox/ws02/deps.edn",
                            :deps-test {"database" {:direct [], :direct-ifc [], :indirect []},
                                        "test-helper" {:direct [], :direct-ifc [], :indirect []},
                                        "user" {:direct ["database" "test-helper"], :direct-ifc [], :indirect []}},
                            :component-names {:src ["database" "test-helper" "user"]
                                              :test ["database" "test-helper" "user"]}
                            :paths {:src ["components/database/src" "components/test-helper/src" "components/user/src" "development/src"]
                                    :test ["components/database/src"
                                           "components/database/test"
                                           "components/test-helper/src"
                                           "components/user/src"
                                           "components/user/test"
                                           "development/src"]}
                            :deps {"database" {:direct [], :direct-ifc [], :indirect []},
                                   "test-helper" {:direct [], :direct-ifc [], :indirect []},
                                   "user" {:direct [], :direct-ifc [], :indirect []}}}],
                :ws-dir "../sandbox/ws02",
                :name "ws02",
                :user-input {:args ["info" "ws-dir:../sandbox/ws02"],
                             :cmd "info",
                             :is-search-for-ws-dir false,
                             :is-all false,
                             :is-dev false,
                             :is-test false,
                             :is-show-project false,
                             :is-show-loc false,
                             :is-run-all-brick-tests false,
                             :is-run-project-tests false,
                             :is-show-resources false,
                             :ws-dir "../sandbox/ws02",
                             :selected-profiles #{},
                             :selected-projects #{},
                             :unnamed-args []},
                :settings {:version "0.2.0-alpha10.issue66.03",
                           :ws-type :toolsdeps2,
                           :ws-schema-version {:breaking 0, :non-breaking 0},
                           :vcs {:name "git"
                                 :auto-add true},
                           :top-namespace "se.example",
                           :interface-ns "interface",
                           :default-profile-name "default",
                           :active-profiles #{"default"},
                           :tag-patterns {:stable "stable-*"
                                          :release "v[0-9]*"}
                           :color-mode "none",
                           :compact-views #{},
                           :user-config-filename "/Users/joakimtengstrand/.config/polylith/config.edn",
                           :empty-character ".",
                           :thousand-separator ",",
                           :user-home "/Users/joakimtengstrand",
                           :m2-dir "/Users/joakimtengstrand/.m2"},
                :ws-reader {:name "polylith-clj",
                            :project-url "https://github.com/polyfy/polylith",
                            :language "Clojure",
                            :type-position "postfix",
                            :slash "/",
                                  :file-extensions [".clj" ".cljs" "cljc"]},
                :paths {:existing ["components/database/src"
                                   "components/database/test"
                                   "components/test-helper/src"
                                   "components/user/src"
                                   "components/user/test"
                                   "development/src"],
                        :missing [],
                        :on-disk ["components/database/src"
                                  "components/database/test"
                                  "components/test-helper/resources"
                                  "components/test-helper/src"
                                  "components/user/src"
                                  "components/user/test"]},
                :messages [],
                :components [{:interface {:name "database", :definitions []},
                              :interface-deps {}
                              :lines-of-code {:src 3, :test 9}
                              :name "database",
                              :type "component",
                              :namespaces {:src [{:name "interface",
                                                  :namespace "se.example.database.interface",
                                                  :file-path "../sandbox/ws02/components/database/src/se/example/database/interface.clj",
                                                  :imports []}]
                                           :test [{:name "stuff",
                                                   :namespace "se.example.database.stuff",
                                                   :file-path "../sandbox/ws02/components/database/test/se/example/database/stuff.clj",
                                                   :imports []}
                                                  {:name "interface-test",
                                                   :namespace "se.example.database.interface-test",
                                                   :file-path "../sandbox/ws02/components/database/test/se/example/database/interface_test.clj",
                                                   :imports ["clojure.test" "se.example.database.interface"]}]}
                              :lib-deps {:src {}}
                              :lib-imports {:test ["clojure.test"]}}
                             {:interface {:name "test-helper", :definitions [{:name "do-stuff", :type "function", :arglist []}]},
                              :interface-deps {}
                              :lines-of-code {:src 3, :test 0}
                              :name "test-helper",
                              :type "component",
                              :namespaces {:src [{:name "interface",
                                                  :namespace "se.example.test-helper.interface",
                                                  :file-path "../sandbox/ws02/components/test-helper/src/se/example/test_helper/interface.clj",
                                                  :imports []}]}
                              :lib-deps {:test {"clj-time" {:version "0.15.2", :type "maven", :size nil}}}
                              :lib-imports {}}
                             {:interface {:name "user", :definitions [{:name "value", :type "data"}]},
                              :interface-deps {:test ["database" "test-helper"]}
                              :lines-of-code {:src 3, :test 9}
                              :name "user",
                              :type "component",
                              :namespaces {:src [{:name "interface",
                                                  :namespace "se.example.user.interface",
                                                  :file-path "../sandbox/ws02/components/user/src/se/example/user/interface.clj",
                                                  :imports []}]
                                           :test [{:name "interface-test",
                                                   :namespace "se.example.user.interface-test",
                                                   :file-path "../sandbox/ws02/components/user/test/se/example/user/interface_test.clj",
                                                   :imports ["clojure.test"
                                                             "se.example.database.stuff"
                                                             "se.example.test-helper.interface"
                                                             "se.example.user.interface"]}]}
                              :lib-deps {}
                              :lib-imports {:test ["clojure.test"]}}]
                :changes {:since "stable",
                          :since-sha "cdf1483635077acab8bb93b300ceec68f624abce",
                          :since-tag "stable-jote",
                          :git-diff-command "git diff cdf1483635077acab8bb93b300ceec68f624abce --name-only",
                          :changed-components ["database" "test-helper"],
                          :changed-bases [],
                          :changed-projects [],
                          :changed-or-affected-projects ["development"],
                          :changed-files ["components/database/src/se/example/database/interface.clj"
                                          "components/test-helper/deps.edn"
                                          "deps.edn"]},
                :bases []
                :profiles [{:name "default"
                            :type "profile"
                            :lib-deps {"clj-commons/fs" {:version "1.6.310", :type "maven", :size 12819}},
                            :component-names [],
                            :base-names [],
                            :project-names []}
                           {:name "extra"
                            :type "profile"
                            :lib-deps {}
                            :component-names []
                            :base-names []
                            :project-names []}]})
