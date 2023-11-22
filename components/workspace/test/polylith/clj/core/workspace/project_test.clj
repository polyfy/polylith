(ns polylith.clj.core.workspace.project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.project :as proj])
  (:refer-clojure :exclude [bases]))

(def components [{:name "change"
                  :type "component"
                  :interface {:name "change"}
                  :interface-deps {:src ["git" "util"]}
                  :namespaces {:src [{:namespace "se.example.change.interface"
                                      :imports ["se.example.git.interface"
                                                "se.example.util.interface"]}]}}
                 {:name "command"
                  :type "component"
                  :interface {:name "command"}
                  :interface-deps {:src ["common" "creator" "deps" "help" "test-runner" "user-config" "util" "workspace"]}
                  :namespaces {:src [{:namespace "se.example.command.interface"
                                      :imports ["se.example.common.interface"
                                                "se.example.creator.interface"
                                                "se.example.deps.interface"
                                                "se.example.help.interface"
                                                "se.example.test-runner.interface"
                                                "se.example.test-user-config.interface"
                                                "se.example.test-util.interface"
                                                "se.example.workspace.interface"]}]}}
                 {:name "cli"
                  :type "component"
                  :interface {:name "cli"}
                  :interface-deps {:src ["change" "command" "file" "util" "workspace" "workspace-clj"]}
                  :namespaces {:src [{:namespace "se.example.cli.interface"
                                      :imports ["se.example.change.interface"
                                                "se.example.command.interface"
                                                "se.example.file.interface"
                                                "se.example.util.interface"
                                                "se.example.workspace.interface"
                                                "se.example.workspace-clj.interface"]}]}}
                 {:name "common"
                  :type "component"
                  :interface {:name "common"}
                  :interface-deps {:src ["util"]}
                  :namespaces {:src [{:namespace "se.example.common.interface"
                                      :imports ["se.example.util.interface"]}]}}
                 {:name "deps"
                  :type "component"
                  :interface {:name "deps"}
                  :interface-deps {:src ["deps" "common" "text-table" "util"]}
                  :namespaces {:src [{:namespace "se.example.deps.interface"
                                      :imports ["se.example.common.interface"
                                                "se.example.text-table.interface"
                                                "se.example.util.interface"]}]}}
                 {:name "file"
                  :type "component"
                  :interface {:name "file"}
                  :interface-deps {:src ["util"]}
                  :namespaces {:src [{:namespace "se.example.file.interface"
                                      :imports ["se.example.util.interface"]}]}}])

(def bases [{:name "cli"
             :type "base"
             :interface-deps {:src ["change" "command" "file" "util" "workspace" "workspace-clj"]}
             :namespaces {:src [{:namespace "se.example.base.core"
                                 :imports ["se.example.change.interface"
                                           "se.example.command.interface"
                                           "se.example.file.interface"
                                           "se.example.util.interface"
                                           "se.example.workspace.interface"
                                           "se.example.workspace-clj.interface"]}]}}])

(def project {:name "development"
              :alias "dev"
              :is-dev true
              :type "project"
              :paths {:src ["bases/cli/src"
                            "components/change/src"
                            "components/command/src"
                            "components/common/src"
                            "components/deps/src"
                            "components/file/src"]
                      :test ["bases/cli/test"
                             "components/change/test"
                             "components/command/test"
                             "test"]}
              :lib-deps {:src {"org.clojure/clojure" #:mvn{:version "1.10.1"}
                               "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}}}
              :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}})

(def brick->loc {"command" {:src 36, :test 0}
                 "cli" {:src 21, :test 0}
                 "deps" {:src 43, :test 51}
                 "file" {:src 80, :test 0}
                 "common" {:src 158, :test 0}
                 "change" {:src 81, :test 25}})

(def brick->lib-imports {"command" {:src ["clojure.pprint"]}
                         "cli" {}
                         "deps" {:src ["clojure.string"]}
                         "file" {:src ["clojure.java.io"]}
                         "common" {:src ["clojure.java.io" "clojure.string"]}
                         "change" {:src ["clojure.set" "clojure.string"]}})

(deftest paths--without-active-profile--returns-expected-map
  (is (= {:alias                    "dev"
          :base-names               {:src ["cli"], :test ["cli"]}
          :component-names          {:src ["change"
                                           "command"
                                           "common"
                                           "deps"
                                           "file"]
                                     :test ["change"
                                            "command"]}
          :is-dev                   true
          :lib-deps                 {:src {"org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                           "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}}}
          :project-lib-deps {:src  {}
                             :test {}}
          :lib-imports              {:src ["clojure.java.io"
                                           "clojure.pprint"
                                           "clojure.set"
                                           "clojure.string"]}
          :lines-of-code {:src 0, :test 0, :total {:src  557, :test 101}}
          :maven-repos              {"central" {:url "https://repo1.maven.org/maven2/"}}
          :name                     "development"
          :paths  {:src ["bases/cli/src"
                         "components/change/src"
                         "components/command/src"
                         "components/common/src"
                         "components/deps/src"
                         "components/file/src"]
                   :test ["bases/cli/test"
                          "components/change/test"
                          "components/command/test"]}
          :type                     "project"
          :unmerged                 {:lib-deps      {:src {"org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                                           "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}}}
                                     :paths {:src ["bases/cli/src"
                                                   "components/change/src"
                                                   "components/command/src"
                                                   "components/common/src"
                                                   "components/deps/src"
                                                   "components/file/src"]
                                             :test ["bases/cli/test"
                                                    "components/change/test"
                                                    "components/command/test"
                                                    "test"]}}}
         (dissoc (proj/enrich-project project "." components bases "se.example." brick->loc brick->lib-imports
                                      {:missing []}
                                      {}
                                      {:projects {"development" {:alias "dev"}}}
                                      #{}
                                      {})
                 :deps))))

(deftest paths--with-active-profile--includes-brick-in-profile
  (is (= {:alias                "dev"
          :base-names           {:src ["cli"],
                                 :test ["cli"]}
          :component-names      {:src ["change"
                                       "command"
                                       "common"
                                       "deps"
                                       "file"
                                       "user"],
                                 :test ["change"
                                        "command"
                                        "user"]}
          :is-dev               true
          :lib-deps             {:src {"clojure.core.matrix"          "net.mikera/core.matrix"
                                       "org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                       "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}}}
          :project-lib-deps {:src  {"clojure.core.matrix" "net.mikera/core.matrix"}
                             :test {}}
          :lib-imports          {:src ["clojure.java.io"
                                       "clojure.pprint"
                                       "clojure.set"
                                       "clojure.string"]}
          :lines-of-code        {:src  0, :test 0, :total  {:src  557, :test 101}}
          :maven-repos          {"central" {:url "https://repo1.maven.org/maven2/"}}
          :name                 "development"
          :paths            {:src ["bases/cli/src"
                                   "components/change/src"
                                   "components/command/src"
                                   "components/common/src"
                                   "components/deps/src"
                                   "components/file/src"
                                   "components/user/resources"
                                   "components/user/src"]
                             :test ["bases/cli/test"
                                    "components/change/test"
                                    "components/command/test"
                                    "components/user/test"]}
          :type                 "project"
          :unmerged             {:lib-deps   {:src {"org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                                    "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}}}
                                 :paths {:src ["bases/cli/src"
                                               "components/change/src"
                                               "components/command/src"
                                               "components/common/src"
                                               "components/deps/src"
                                               "components/file/src"]
                                         :test ["bases/cli/test"
                                                "components/change/test"
                                                "components/command/test"
                                                "test"]}}}
         (dissoc (proj/enrich-project project "." components bases "se.example." brick->loc brick->lib-imports
                                      {:missing []}
                                      {}
                                      {:active-profiles ["default"]
                                       :profile-to-settings {"default" {:paths ["components/user/src"
                                                                                "components/user/resources"
                                                                                "components/user/test"]
                                                                        :lib-deps {"clojure.core.matrix"
                                                                                   "net.mikera/core.matrix"}}}
                                       :projects {"development" {:alias "dev", :test []}}}
                                      #{}
                                      {})
                 :deps))))
