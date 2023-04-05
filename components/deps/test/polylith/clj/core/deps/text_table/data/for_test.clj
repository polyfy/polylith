(ns polylith.clj.core.deps.text-table.data.for-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.interface :as common]))

(def workspace '{:interfaces [{:name "a-okay", :type "interface", :definitions [], :implementing-components ["a-okay"]}
                              {:name "b-fail", :type "interface", :definitions [], :implementing-components ["b-fail"]}
                              {:name "company",
                               :type "interface",
                               :definitions [{:name "abc", :type "data"}],
                               :implementing-components ["company"]}
                              {:name "util",
                               :type "interface",
                               :definitions [{:name "abc", :type "data"}],
                               :implementing-components ["util"]}],
                 :projects [{:lines-of-code {:src 0, :test 7, :total {:src 12, :test 36}},
                             :namespaces {:test [{:name "project.failing-test.test-setup",
                                                  :namespace "project.failing-test.test-setup",
                                                  :file-path "projects/failing-test/test/project/failing_test/test_setup.clj",
                                                  :imports []}]},
                             :base-names {},
                             :lib-imports {:test ["clojure.test"]},
                             :is-dev false,
                             :name "failing-test",
                             :paths {:src ["components/a-okay/src" "components/b-fail/src" "components/util/src"],
                                     :test ["components/a-okay/test"
                                            "components/b-fail/test"
                                            "components/util/test"
                                            "projects/failing-test/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "failing",
                             :project-dir "examples/for-test/projects/failing-test",
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps" {:version "0.16.1281", :type "maven", :size 58704}}},
                             :config-filename "examples/for-test/projects/failing-test/deps.edn",
                             :component-names {:src ["a-okay" "b-fail" "util"], :test ["a-okay" "b-fail" "util"]},
                             :deps {"a-okay" {:src {:direct ["util"]}, :test {:direct ["util"]}},
                                    "b-fail" {:src {}, :test {}},
                                    "util" {:src {}, :test {}}}}
                            {:lines-of-code {:src 0, :test 7, :total {:src 12, :test 36}},
                             :namespaces {:test [{:name "project.failing-test-teardown-fails.test-setup",
                                                  :namespace "project.failing-test-teardown-fails.test-setup",
                                                  :file-path "projects/failing-test-teardown-fails/test/project/failing_test_teardown_fails/test_setup.clj",
                                                  :imports []}]},
                             :base-names {},
                             :lib-imports {:test ["clojure.test"]},
                             :is-dev false,
                             :name "failing-test-teardown-fails",
                             :paths {:src ["components/a-okay/src" "components/b-fail/src" "components/util/src"],
                                     :test ["components/a-okay/test"
                                            "components/b-fail/test"
                                            "components/util/test"
                                            "projects/failing-test-teardown-fails/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "failing-x2",
                             :project-dir "examples/for-test/projects/failing-test-teardown-fails",
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps" {:version "0.16.1281", :type "maven", :size 58704}}},
                             :config-filename "examples/for-test/projects/failing-test-teardown-fails/deps.edn",
                             :component-names {:src ["a-okay" "b-fail" "util"], :test ["a-okay" "b-fail" "util"]},
                             :deps {"a-okay" {:src {:direct ["util"]}, :test {:direct ["util"]}},
                                    "b-fail" {:src {}, :test {}},
                                    "util" {:src {}, :test {}}}}
                            {:lines-of-code {:src 0, :test 7, :total {:src 24, :test 50}},
                             :namespaces {:test [{:name "project.okay.test-setup",
                                                  :namespace "project.okay.test-setup",
                                                  :file-path "projects/okay/test/project/okay/test_setup.clj",
                                                  :imports []}]},
                             :base-names {},
                             :lib-imports {:src ["clojure.string"], :test ["clojure.test"]},
                             :is-dev false,
                             :name "okay",
                             :paths {:src ["components/a-okay/src"
                                           "components/company/clj"
                                           "components/company/cljc"
                                           "components/company/resources"
                                           "components/util/src"],
                                     :test ["components/a-okay/test"
                                            "components/company/test"
                                            "components/company/test2"
                                            "components/util/test"
                                            "projects/okay/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "okay",
                             :project-dir "examples/for-test/projects/okay",
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps" {:version "0.16.1281", :type "maven", :size 58704}}},
                             :config-filename "examples/for-test/projects/okay/deps.edn",
                             :component-names {:src ["a-okay" "company" "util"], :test ["a-okay" "company" "util"]},
                             :deps {"a-okay" {:src {:direct ["util"]}, :test {:direct ["util"]}},
                                    "company" {:src {}, :test {}},
                                    "util" {:src {}, :test {}}}}
                            {:lines-of-code {:src 0, :test 7, :total {:src 6, :test 12}},
                             :namespaces {:test [{:name "project.setup-fails.test-setup",
                                                  :namespace "project.setup-fails.test-setup",
                                                  :file-path "projects/setup-fails/test/project/setup_fails/test_setup.clj",
                                                  :imports []}]},
                             :base-names {},
                             :lib-imports {:test ["clojure.test"]},
                             :is-dev false,
                             :name "setup-fails",
                             :paths {:src ["components/util/src"], :test ["components/util/test" "projects/setup-fails/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "setup-fails",
                             :project-dir "examples/for-test/projects/setup-fails",
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps" {:version "0.16.1281", :type "maven", :size 58704}}},
                             :config-filename "examples/for-test/projects/setup-fails/deps.edn",
                             :component-names {:src ["util"], :test ["util"]},
                             :deps {"util" {:src {}, :test {}}}}
                            {:lines-of-code {:src 0, :test 7, :total {:src 10, :test 24}},
                             :namespaces {:test [{:name "project.teardown-fails.test-setup",
                                                  :namespace "project.teardown-fails.test-setup",
                                                  :file-path "projects/teardown-fails/test/project/teardown_fails/test_setup.clj",
                                                  :imports []}]},
                             :base-names {},
                             :lib-imports {:test ["clojure.test"]},
                             :is-dev false,
                             :name "teardown-fails",
                             :paths {:src ["components/a-okay/src" "components/util/src"],
                                     :test ["components/a-okay/test" "components/util/test" "projects/teardown-fails/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "teardown-fails",
                             :project-dir "examples/for-test/projects/teardown-fails",
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps" {:version "0.16.1281", :type "maven", :size 58704}}},
                             :config-filename "examples/for-test/projects/teardown-fails/deps.edn",
                             :component-names {:src ["a-okay" "util"], :test ["a-okay" "util"]},
                             :deps {"a-okay" {:src {:direct ["util"]}, :test {:direct ["util"]}}, "util" {:src {}, :test {}}}}
                            {:lines-of-code {:src 0, :test 7, :total {:src 24, :test 50}},
                             :namespaces {:test [{:name "project.x-okay.test-setup",
                                                  :namespace "project.x-okay.test-setup",
                                                  :file-path "projects/x-okay/test/project/x_okay/test_setup.clj",
                                                  :imports []}]},
                             :base-names {},
                             :lib-imports {:src ["clojure.string"], :test ["clojure.test"]},
                             :is-dev false,
                             :name "x-okay",
                             :paths {:src ["components/a-okay/src"
                                           "components/company/clj"
                                           "components/company/cljc"
                                           "components/company/resources"
                                           "components/util/src"],
                                     :test ["components/a-okay/test"
                                            "components/company/test"
                                            "components/company/test2"
                                            "components/util/test"
                                            "projects/x-okay/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "x-ok",
                             :project-dir "examples/for-test/projects/x-okay",
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.10.1", :type "maven", :size 3908431},
                                              "org.clojure/tools.deps" {:version "0.16.1281", :type "maven", :size 58704}}},
                             :config-filename "examples/for-test/projects/x-okay/deps.edn",
                             :component-names {:src ["a-okay" "company" "util"], :test ["a-okay" "company" "util"]},
                             :deps {"a-okay" {:src {:direct ["util"]}, :test {:direct ["util"]}},
                                    "company" {:src {}, :test {}},
                                    "util" {:src {}, :test {}}}}
                            {:lines-of-code {:src 0, :test 0, :total {:src 39, :test 98}},
                             :namespaces {},
                             :base-names {:src ["cli" "helper" "worker"], :test ["cli" "helper"]},
                             :lib-imports {:src ["clojure.string" "com.for.test.cli.impl" "com.for.test.worker.core"],
                                           :test ["clojure.test" "com.for.test.helper.core" "com.for.test.helper.core-test"]},
                             :is-dev true,
                             :name "development",
                             :paths {:src ["bases/cli/src"
                                           "bases/helper/src"
                                           "bases/worker/src"
                                           "components/a-okay/src"
                                           "components/b-fail/src"
                                           "components/company/clj"
                                           "components/company/cljc"
                                           "components/company/resources"
                                           "components/util/src"
                                           "development/src"],
                                     :test ["bases/cli/test"
                                            "bases/helper/test"
                                            "components/a-okay/test"
                                            "components/b-fail/test"
                                            "components/company/test"
                                            "components/company/test2"
                                            "components/util/test"
                                            "projects/failing-test-teardown-fails/test"
                                            "projects/failing-test/test"
                                            "projects/okay/test"
                                            "projects/setup-fails/test"
                                            "projects/teardown-fails/test"
                                            "projects/x-okay/test"]},
                             :type "project",
                             :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                           "clojars" {:url "https://repo.clojars.org/"}},
                             :alias "dev",
                             :project-dir "examples/for-test/development",
                             :unmerged {:paths {:src ["bases/cli/src"
                                                      "bases/helper/src"
                                                      "bases/worker/src"
                                                      "components/a-okay/src"
                                                      "components/b-fail/src"
                                                      "components/company/clj"
                                                      "components/company/cljc"
                                                      "components/company/resources"
                                                      "components/util/src"
                                                      "development/src"],
                                                :test ["bases/cli/test"
                                                       "bases/helper/test"
                                                       "components/a-okay/test"
                                                       "components/b-fail/test"
                                                       "components/company/test"
                                                       "components/company/test2"
                                                       "components/util/test"
                                                       "projects/failing-test-teardown-fails/test"
                                                       "projects/failing-test/test"
                                                       "projects/okay/test"
                                                       "projects/setup-fails/test"
                                                       "projects/teardown-fails/test"
                                                       "projects/x-okay/test"]},
                                        :lib-deps {:src {"org.clojure/clojure" {:version "1.11.1", :type "maven", :size 4105111}}}},
                             :lib-deps {:src {"org.clojure/clojure" {:version "1.11.1", :type "maven", :size 4105111}}},
                             :config-filename "examples/for-test/deps.edn",
                             :component-names {:src ["a-okay" "b-fail" "company" "util"], :test ["a-okay" "b-fail" "company" "util"]},
                             :deps {"cli" {:src {:direct ["a-okay" "worker"], :indirect ["util"]}, :test {:direct ["helper" "util"]}},
                                    "helper" {:src {}, :test {}},
                                    "worker" {:src {}, :test {}},
                                    "a-okay" {:src {:direct ["util"]}, :test {:direct ["util"]}},
                                    "b-fail" {:src {}, :test {}},
                                    "company" {:src {}, :test {}},
                                    "util" {:src {}, :test {}}}}],
                 :ws-dir "examples/for-test",
                 :name "for-test",
                 :user-input {:args ["info" "ws-dir:examples/for-test" "replace:/Users/joakimtengstrand:USER-HOME"],
                              :cmd "info",
                              :is-commit false,
                              :is-tap false,
                              :is-search-for-ws-dir false,
                              :is-all false,
                              :is-compact false,
                              :is-dev false,
                              :is-latest-sha false,
                              :is-no-changes false,
                              :is-no-exit false,
                              :is-outdated false,
                              :is-show-brick false,
                              :is-show-workspace false,
                              :is-show-project false,
                              :is-show-loc false,
                              :is-run-all-brick-tests false,
                              :is-run-project-tests false,
                              :is-show-resources false,
                              :is-verbose false,
                              :replace [{:from "/Users/joakimtengstrand", :to "USER-HOME"}],
                              :ws-dir "examples/for-test",
                              :selected-profiles #{},
                              :selected-projects #{},
                              :unnamed-args []},
                 :settings {:vcs {:name "git",
                                  :is-git-repo true,
                                  :branch "issue-293",
                                  :git-root "/Users/joakimtengstrand/source/polylith",
                                  :auto-add false,
                                  :stable-since {:tag "stable-master", :sha "441e71050d506b7cee8fb64e107ff0fafcf36472"},
                                  :polylith {:repo "https://github.com/polyfy/polylith.git", :branch "master"}},
                            :top-namespace "com.for.test",
                            :interface-ns "interface",
                            :default-profile-name "default",
                            :active-profiles #{},
                            :tag-patterns {:stable "stable-*", :release "v[0-9]*"},
                            :color-mode "none",
                            :compact-views #{},
                            :user-config-filename "/Users/joakimtengstrand/.config/polylith/config.edn",
                            :empty-character ".",
                            :thousand-separator ",",
                            :profile-to-settings {},
                            :projects {"development" {:alias "dev",
                                                      :test {:create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]}},
                                       "failing-test" {:alias "failing",
                                                       :test {:setup-fn project.failing-test.test-setup/setup,
                                                              :teardown-fn project.failing-test.test-setup/teardown,
                                                              :create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]},
                                                       :necessary ["a-okay" "b-fail" "util"]},
                                       "failing-test-teardown-fails" {:alias "failing-x2",
                                                                      :test {:setup-fn project.failing-test-teardown-fails.test-setup/setup,
                                                                             :teardown-fn project.failing-test-teardown-fails.test-setup/teardown,
                                                                             :create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]},
                                                                      :necessary ["a-okay" "b-fail" "util"]},
                                       "okay" {:alias "okay",
                                               :test {:setup-fn project.okay.test-setup/setup,
                                                      :teardown-fn project.okay.test-setup/teardown,
                                                      :create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]},
                                               :necessary ["a-okay" "b-fail" "company" "util"]},
                                       "service" {:alias "service",
                                                  :test {:create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]}},
                                       "setup-fails" {:alias "setup-fails",
                                                      :test {:setup-fn project.setup-fails.test-setup/setup,
                                                             :teardown-fn project.setup-fails.test-setup/teardown,
                                                             :create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]},
                                                      :necessary ["util"]},
                                       "teardown-fails" {:alias "teardown-fails",
                                                         :test {:setup-fn project.teardown-fails.test-setup/setup,
                                                                :teardown-fn project.teardown-fails.test-setup/teardown,
                                                                :create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]},
                                                         :necessary ["a-okay" "util"]},
                                       "x-okay" {:alias "x-ok",
                                                 :test {:setup-fn project.x-okay.test-setup/setup,
                                                        :teardown-fn project.x-okay.test-setup/teardown,
                                                        :create-test-runner [polylith.clj.core.clojure-test-test-runner.interface/create]},
                                                 :necessary ["a-okay" "company" "util"]}},
                            :user-home "/Users/joakimtengstrand",
                            :m2-dir "/Users/joakimtengstrand/.m2"},
                 :ws-reader {:name "polylith-clj",
                             :project-url "https://github.com/polyfy/polylith",
                             :language "Clojure",
                             :type-position "postfix",
                             :file-extensions ["clj" "cljc"]},
                 :paths {:existing ["bases/cli/src"
                                    "bases/cli/test"
                                    "bases/helper/src"
                                    "bases/helper/test"
                                    "bases/worker/src"
                                    "components/a-okay/src"
                                    "components/a-okay/test"
                                    "components/b-fail/src"
                                    "components/b-fail/test"
                                    "components/company/clj"
                                    "components/company/cljc"
                                    "components/company/resources"
                                    "components/company/test"
                                    "components/company/test2"
                                    "components/util/src"
                                    "components/util/test"
                                    "development/src"
                                    "projects/failing-test-teardown-fails/test"
                                    "projects/failing-test/test"
                                    "projects/okay/test"
                                    "projects/setup-fails/test"
                                    "projects/teardown-fails/test"
                                    "projects/x-okay/test"],
                         :missing [],
                         :on-disk ["bases/cli/src"
                                   "bases/cli/test"
                                   "bases/helper/src"
                                   "bases/helper/test"
                                   "bases/worker/src"
                                   "components/a-okay/src"
                                   "components/a-okay/test"
                                   "components/b-fail/src"
                                   "components/b-fail/test"
                                   "components/company/clj"
                                   "components/company/cljc"
                                   "components/company/resources"
                                   "components/company/test"
                                   "components/company/test2"
                                   "components/util/src"
                                   "components/util/test"
                                   "projects/failing-test-teardown-fails/test"
                                   "projects/failing-test/test"
                                   "projects/okay/test"
                                   "projects/setup-fails/test"
                                   "projects/teardown-fails/test"
                                   "projects/x-okay/test"]},
                 :ws-local-dir "examples/for-test",
                 :ws-type "toolsdeps2",
                 :messages [],
                 :components [{:lines-of-code {:src 2, :test 6},
                               :interface {:name "a-okay", :definitions []},
                               :namespaces {:src [{:name "interface",
                                                   :namespace "com.for.test.a-okay.interface",
                                                   :file-path "components/a-okay/src/com/for/test/a_okay/interface.clj",
                                                   :imports ["com.for.test.util.interface"]}],
                                            :test [{:name "interface-test",
                                                    :namespace "com.for.test.a-okay.interface-test",
                                                    :file-path "components/a-okay/test/com/for/test/a_okay/interface_test.clj",
                                                    :imports ["clojure.test" "com.for.test.a-okay.interface"]}]},
                               :lib-imports {:test ["clojure.test"]},
                               :name "a-okay",
                               :paths {:src ["src"], :test ["test"]},
                               :type "component",
                               :interface-deps {:src ["util"], :test []},
                               :lib-deps {}}
                              {:lines-of-code {:src 1, :test 6},
                               :interface {:name "b-fail", :definitions []},
                               :namespaces {:src [{:name "interface",
                                                   :namespace "com.for.test.b-fail.interface",
                                                   :file-path "components/b-fail/src/com/for/test/b_fail/interface.clj",
                                                   :imports []}],
                                            :test [{:name "interface-test",
                                                    :namespace "com.for.test.b-fail.interface-test",
                                                    :file-path "components/b-fail/test/com/for/test/b_fail/interface_test.clj",
                                                    :imports ["clojure.test" "com.for.test.b-fail.interface"]}]},
                               :lib-imports {:test ["clojure.test"]},
                               :name "b-fail",
                               :paths {:src ["src"], :test ["test"]},
                               :type "component",
                               :interface-deps {:src [], :test []},
                               :lib-deps {}}
                              {:lines-of-code {:src 7, :test 13},
                               :interface {:name "company", :definitions [{:name "abc", :type "data"}]},
                               :namespaces {:src [{:name "interface",
                                                   :namespace "com.for.test.company.interface",
                                                   :file-path "components/company/clj/com/for/test/company/interface.clj",
                                                   :imports []}
                                                  {:name "shared",
                                                   :namespace "com.for.test.company.shared",
                                                   :file-path "components/company/cljc/com/for/test/company/shared.cljc",
                                                   :imports []}
                                                  {:name "interface",
                                                   :namespace "com.for.test.company.interface",
                                                   :file-path "components/company/cljc/com/for/test/company/interface.cljc",
                                                   :imports ["clojure.string"]}],
                                            :test [{:name "interface-test",
                                                    :namespace "com.for.test.company.interface-test",
                                                    :file-path "components/company/test/com/for/test/company/interface_test.cljc",
                                                    :imports ["clojure.test"]}
                                                   {:name "interface-test",
                                                    :namespace "com.for.test.company.interface-test",
                                                    :file-path "components/company/test2/com/for/test/company/interface_test.clj",
                                                    :imports ["clojure.test"]}]},
                               :lib-imports {:src ["clojure.string"], :test ["clojure.test"]},
                               :name "company",
                               :paths {:src ["clj" "cljc" "resources"], :test ["test" "test2"]},
                               :type "component",
                               :interface-deps {:src [], :test []},
                               :lib-deps {}}
                              {:lines-of-code {:src 3, :test 6},
                               :interface {:name "util", :definitions [{:name "abc", :type "data"}]},
                               :namespaces {:src [{:name "interface",
                                                   :namespace "com.for.test.util.interface",
                                                   :file-path "components/util/src/com/for/test/util/interface.clj",
                                                   :imports []}],
                                            :test [{:name "interface-test",
                                                    :namespace "com.for.test.util.interface-test",
                                                    :file-path "components/util/test/com/for/test/util/interface_test.clj",
                                                    :imports ["clojure.test" "com.for.test.util.interface"]}]},
                               :lib-imports {:test ["clojure.test"]},
                               :name "util",
                               :paths {:src ["src"], :test ["test"]},
                               :type "component",
                               :interface-deps {:src [], :test []},
                               :lib-deps {}}],
                 :changes {:since "stable",
                           :since-sha "441e71050d506b7cee8fb64e107ff0fafcf36472",
                           :since-tag "stable-master",
                           :changed-files [".idea/runConfigurations.xml"
                                           "bases/cli/deps.edn"
                                           "bases/cli/src/com/for/test/cli/core.clj"
                                           "bases/cli/src/com/for/test/cli/impl.clj"
                                           "bases/cli/test/com/for/test/cli/core_test.clj"
                                           "bases/helper/deps.edn"
                                           "bases/helper/src/com/for/test/helper/core.clj"
                                           "bases/helper/test/com/for/test/helper/core_test.clj"
                                           "bases/worker/deps.edn"
                                           "bases/worker/src/com/for/test/worker/core.clj"
                                           "components/a-okay/src/com/for/test/a_okay/interface.clj"
                                           "components/company/cljc/com/for/test/company/interface.cljc"
                                           "components/util/src/com/for/test/util/interface.clj"
                                           "components/util/test/com/for/test/util/interface_test.clj"
                                           "deps.edn"
                                           "projects/failing-test-teardown-fails/deps.edn"
                                           "projects/failing-test/deps.edn"
                                           "projects/okay/deps.edn"
                                           "projects/setup-fails/deps.edn"
                                           "projects/teardown-fails/deps.edn"
                                           "projects/x-okay/deps.edn"
                                           "workspace.edn"],
                           :git-diff-command "git diff 441e71050d506b7cee8fb64e107ff0fafcf36472 --name-only",
                           :changed-components ["a-okay" "company" "util"],
                           :changed-bases ["cli" "helper" "worker"],
                           :changed-projects ["failing-test"
                                              "failing-test-teardown-fails"
                                              "okay"
                                              "setup-fails"
                                              "teardown-fails"
                                              "x-okay"],
                           :changed-or-affected-projects ["development"
                                                          "failing-test"
                                                          "failing-test-teardown-fails"
                                                          "okay"
                                                          "setup-fails"
                                                          "teardown-fails"
                                                          "x-okay"],
                           :project-to-indirect-changes {"failing-test" {:src [], :test []},
                                                         "failing-test-teardown-fails" {:src [], :test []},
                                                         "okay" {:src [], :test []},
                                                         "setup-fails" {:src [], :test []},
                                                         "teardown-fails" {:src [], :test []},
                                                         "x-okay" {:src [], :test []},
                                                         "development" {:src [], :test []}},
                           :project-to-bricks-to-test {"development" [],
                                                       "failing-test" ["a-okay" "b-fail" "util"],
                                                       "failing-test-teardown-fails" ["a-okay" "b-fail" "util"],
                                                       "okay" ["a-okay" "company" "util"],
                                                       "setup-fails" ["util"],
                                                       "teardown-fails" ["a-okay" "util"],
                                                       "x-okay" ["a-okay" "company" "util"]},
                           :project-to-projects-to-test {"failing-test" [],
                                                         "failing-test-teardown-fails" [],
                                                         "okay" [],
                                                         "setup-fails" [],
                                                         "teardown-fails" [],
                                                         "x-okay" [],
                                                         "development" []}},
                 :version {:release {:name "0.2.18-issue293-01",
                                     :major 0,
                                     :minor 2,
                                     :patch 18,
                                     :revision "issue293-01",
                                     :date "2023-04-04"},
                           :ws {:type :toolsdeps2, :breaking 2, :non-breaking 0}},
                 :bases [{:lines-of-code {:src 5, :test 10},
                          :namespaces {:src [{:name "core",
                                              :namespace "com.for.test.cli.core",
                                              :file-path "bases/cli/src/com/for/test/cli/core.clj",
                                              :imports ["com.for.test.a-okay.interface"
                                                        "com.for.test.cli.impl"
                                                        "com.for.test.worker.core"]}
                                             {:name "impl",
                                              :namespace "com.for.test.cli.impl",
                                              :file-path "bases/cli/src/com/for/test/cli/impl.clj",
                                              :imports []}],
                                       :test [{:name "core-test",
                                               :namespace "com.for.test.cli.core-test",
                                               :file-path "bases/cli/test/com/for/test/cli/core_test.clj",
                                               :imports ["clojure.test" "com.for.test.helper.core-test" "com.for.test.util.interface"]}]},
                          :lib-imports {:src ["com.for.test.cli.impl" "com.for.test.worker.core"],
                                        :test ["clojure.test" "com.for.test.helper.core-test"]},
                          :name "cli",
                          :paths {:src ["src"], :test ["test"]},
                          :type "base",
                          :interface-deps {:src ["a-okay"], :test ["util"]},
                          :lib-deps {},
                          :base-deps {:src ["worker"], :test ["helper"]}}
                         {:lines-of-code {:src 1, :test 8},
                          :namespaces {:src [{:name "core",
                                              :namespace "com.for.test.helper.core",
                                              :file-path "bases/helper/src/com/for/test/helper/core.clj",
                                              :imports []}],
                                       :test [{:name "core-test",
                                               :namespace "com.for.test.helper.core-test",
                                               :file-path "bases/helper/test/com/for/test/helper/core_test.clj",
                                               :imports ["clojure.test" "com.for.test.helper.core"]}]},
                          :lib-imports {:test ["clojure.test" "com.for.test.helper.core"]},
                          :name "helper",
                          :paths {:src ["src"], :test ["test"]},
                          :type "base",
                          :interface-deps {:src [], :test []},
                          :lib-deps {},
                          :base-deps {:src [], :test []}}
                         {:lines-of-code {:src 1, :test 0},
                          :namespaces {:src [{:name "core",
                                              :namespace "com.for.test.worker.core",
                                              :file-path "bases/worker/src/com/for/test/worker/core.clj",
                                              :imports []}]},
                          :lib-imports {},
                          :name "worker",
                          :paths {:src ["src"], :test []},
                          :type "base",
                          :interface-deps {:src [], :test []},
                          :lib-deps {},
                          :base-deps {:src [], :test []}}],
                 :configs {:components [{:config {:paths ["src"], :deps {}, :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                         :name "a-okay"}
                                        {:config {:paths ["src"], :deps {}, :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                         :name "b-fail"}
                                        {:config {:paths ["clj" "cljc" "resources"],
                                                  :deps {},
                                                  :aliases {:src-paths ["clj" "cljc"],
                                                            :resources-path ["resources"],
                                                            :test-paths ["test" "test2"],
                                                            :test {:extra-paths ["test" "test2"], :extra-deps {}}}},
                                         :name "company"}
                                        {:config {:paths ["src"], :deps {}, :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                         :name "util"}],
                           :bases [{:config {:paths ["src"], :deps {}, :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                    :name "cli"}
                                   {:config {:paths ["src"], :deps {}, :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                    :name "helper"}
                                   {:config {:paths ["src"], :deps {}, :aliases {:test {:extra-paths [], :extra-deps {}}}},
                                    :name "worker"}],
                           :projects [{:config {:aliases {:dev {:extra-paths ["development/src"
                                                                              "components/company/clj"
                                                                              "components/company/cljc"
                                                                              "components/company/resources"
                                                                              "components/a-okay/src"
                                                                              "components/b-fail/src"
                                                                              "components/util/src"
                                                                              "bases/cli/src"
                                                                              "bases/helper/src"
                                                                              "bases/worker/src"],
                                                                :extra-deps #:org.clojure{clojure #:mvn{:version "1.11.1"}}},
                                                          :test {:extra-paths ["components/company/test"
                                                                               "components/company/test2"
                                                                               "components/a-okay/test"
                                                                               "components/b-fail/test"
                                                                               "components/util/test"
                                                                               "bases/cli/test"
                                                                               "bases/helper/test"
                                                                               "projects/failing-test/test"
                                                                               "projects/failing-test-teardown-fails/test"
                                                                               "projects/okay/test"
                                                                               "projects/setup-fails/test"
                                                                               "projects/teardown-fails/test"
                                                                               "projects/x-okay/test"]},
                                                          :poly {:main-opts ["-m" "polylith.clj.core.poly-cli.core"],
                                                                 :extra-deps #:polyfy{polylith {:git/url "https://github.com/polyfy/polylith",
                                                                                                :sha "75140d3101805331ba40b7982a69702b11a858e8",
                                                                                                :deps/root "projects/poly"}}}}},
                                       :name "development"}
                                      {:config {:deps {poly/a-okay #:local{:root "../../components/a-okay"},
                                                       poly/b-fail #:local{:root "../../components/b-fail"},
                                                       poly/util #:local{:root "../../components/util"},
                                                       org.clojure/clojure #:mvn{:version "1.10.1"},
                                                       org.clojure/tools.deps #:mvn{:version "0.16.1281"}},
                                                :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                       :name "failing-test"}
                                      {:config {:deps {poly/a-okay #:local{:root "../../components/a-okay"},
                                                       poly/b-fail #:local{:root "../../components/b-fail"},
                                                       poly/util #:local{:root "../../components/util"},
                                                       org.clojure/clojure #:mvn{:version "1.10.1"},
                                                       org.clojure/tools.deps #:mvn{:version "0.16.1281"}},
                                                :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                       :name "failing-test-teardown-fails"}
                                      {:config {:deps {poly/a-okay #:local{:root "../../components/a-okay"},
                                                       poly/company #:local{:root "../../components/company"},
                                                       poly/util #:local{:root "../../components/util"},
                                                       org.clojure/clojure #:mvn{:version "1.10.1"},
                                                       org.clojure/tools.deps #:mvn{:version "0.16.1281"}},
                                                :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                       :name "okay"}
                                      {:config {:deps {poly/util #:local{:root "../../components/util"},
                                                       org.clojure/clojure #:mvn{:version "1.10.1"},
                                                       org.clojure/tools.deps #:mvn{:version "0.16.1281"}},
                                                :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                       :name "setup-fails"}
                                      {:config {:deps {poly/a-okay #:local{:root "../../components/a-okay"},
                                                       poly/util #:local{:root "../../components/util"},
                                                       org.clojure/clojure #:mvn{:version "1.10.1"},
                                                       org.clojure/tools.deps #:mvn{:version "0.16.1281"}},
                                                :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                       :name "teardown-fails"}
                                      {:config {:deps {poly/a-okay #:local{:root "../../components/a-okay"},
                                                       poly/company #:local{:root "../../components/company"},
                                                       poly/util #:local{:root "../../components/util"},
                                                       org.clojure/clojure #:mvn{:version "1.10.1"},
                                                       org.clojure/tools.deps #:mvn{:version "0.16.1281"}},
                                                :aliases {:test {:extra-paths ["test"], :extra-deps {}}}},
                                       :name "x-okay"}],
                           :user {:color-mode "dark", :empty-character ".", :thousand-separator ","},
                           :workspace {:top-namespace "com.for.test",
                                       :interface-ns "interface",
                                       :default-profile-name "default",
                                       :compact-views #{},
                                       :vcs {:name "git", :auto-add false},
                                       :tag-patterns {:stable "stable-*", :release "v[0-9]*"},
                                       :projects {"development" {:alias "dev"},
                                                  "failing-test" {:alias "failing",
                                                                  :test {:setup-fn project.failing-test.test-setup/setup,
                                                                         :teardown-fn project.failing-test.test-setup/teardown},
                                                                  :necessary ["a-okay" "b-fail" "util"]},
                                                  "failing-test-teardown-fails" {:alias "failing-x2",
                                                                                 :test {:setup-fn project.failing-test-teardown-fails.test-setup/setup,
                                                                                        :teardown-fn project.failing-test-teardown-fails.test-setup/teardown},
                                                                                 :necessary ["a-okay" "b-fail" "util"]},
                                                  "service" {:alias "service"},
                                                  "okay" {:alias "okay",
                                                          :test {:setup-fn project.okay.test-setup/setup,
                                                                 :teardown-fn project.okay.test-setup/teardown},
                                                          :necessary ["a-okay" "b-fail" "company" "util"]},
                                                  "setup-fails" {:alias "setup-fails",
                                                                 :test {:setup-fn project.setup-fails.test-setup/setup,
                                                                        :teardown-fn project.setup-fails.test-setup/teardown},
                                                                 :necessary ["util"]},
                                                  "teardown-fails" {:alias "teardown-fails",
                                                                    :test {:setup-fn project.teardown-fails.test-setup/setup,
                                                                           :teardown-fn project.teardown-fails.test-setup/teardown},
                                                                    :necessary ["a-okay" "util"]},
                                                  "x-okay" {:alias "x-ok",
                                                            :test {:setup-fn project.x-okay.test-setup/setup,
                                                                   :teardown-fn project.x-okay.test-setup/teardown},
                                                            :necessary ["a-okay" "company" "util"]}}}}})

(def development (common/find-project "development" (:projects workspace)))

(def util (common/find-component "util" (:components workspace)))

(def cli (common/find-base "cli" (:bases workspace)))

(def worker (common/find-base "worker" (:bases workspace)))
