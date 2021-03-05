(ns polylith.clj.core.workspace.project-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.project :as proj])
  (:refer-clojure :exclude [bases]))

(def components [{:name "change"
                  :interface {:name "change"}
                  :interface-deps ["git" "util"]}
                 {:name "command"
                  :interface {:name "command"}
                  :interface-deps ["common" "creator" "deps" "help" "test-runner" "user-config" "util" "workspace"]}
                 {:name "common"
                  :interface {:name "common"}
                  :interface-deps ["util"]}
                 {:name "deps"
                  :interface {:name "deps"}
                  :interface-deps ["common" "text-table" "util"]}
                 {:name "file"
                  :interface {:name "file"}
                  :interface-deps ["util"]}])

(def bases [{:name "cli"
             :interface-deps ["change" "command" "file" "util" "workspace" "workspace-clj"]}])

(def project {:name "development"
              :alias "dev"
              :is-dev true
              :type "project"
              :src-paths ["bases/cli/src"
                          "components/change/src"
                          "components/command/src"
                          "components/common/src"
                          "components/deps/src"
                          "components/file/src"]
              :test-paths ["bases/cli/test"
                           "components/change/test"
                           "components/command/test"
                           "test"]
              :lib-imports ["clojure.java.io"
                            "clojure.java.shell"
                            "clojure.pprint"
                            "clojure.set"
                            "clojure.string"
                            "clojure.tools.deps.alpha"
                            "clojure.tools.deps.alpha.util.maven"
                            "clojure.walk"]
              :lib-imports-test ["clojure.string" "clojure.tools.deps.alpha.util.maven"]
              :lib-deps {"org.clojure/clojure" #:mvn{:version "1.10.1"}
                         "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
              :lib-deps-test {}
              :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}})

(def brick->loc {"command" {:lines-of-code-src 36, :lines-of-code-test 0}
                 "cli" {:lines-of-code-src 21, :lines-of-code-test 0}
                 "deps" {:lines-of-code-src 43, :lines-of-code-test 51}
                 "file" {:lines-of-code-src 80, :lines-of-code-test 0}
                 "common" {:lines-of-code-src 158, :lines-of-code-test 0}
                 "change" {:lines-of-code-src 81, :lines-of-code-test 25}})

(def brick->lib-imports {"command" {:lib-imports-src ["clojure.pprint"], :lib-imports-test []}
                         "cli" {:lib-imports-src [], :lib-imports-test []}
                         "deps" {:lib-imports-src ["clojure.string"], :lib-imports-test []}
                         "file" {:lib-imports-src ["clojure.java.io"], :lib-imports-test []}
                         "common" {:lib-imports-src ["clojure.java.io" "clojure.string"], :lib-imports-test []}
                         "change" {:lib-imports-src ["clojure.set" "clojure.string"], :lib-imports-test []}})

(deftest paths--without-active-profile--returns-expected-map
  (is (= {:alias                    "dev"
          :base-names               ["cli"]
          :component-names          ["change"
                                     "command"
                                     "common"
                                     "deps"
                                     "file"]
          :deps                     {"change"  {:direct     []
                                                :direct-ifc ["git"
                                                             "util"]
                                                :indirect   []}
                                     "cli"     {:direct     ["change"
                                                             "command"
                                                             "file"]
                                                :direct-ifc ["util"
                                                             "workspace"
                                                             "workspace-clj"]
                                                :indirect   ["common"
                                                             "deps"]}
                                     "command" {:direct     ["common"
                                                             "deps"]
                                                :direct-ifc ["creator"
                                                             "help"
                                                             "test-runner"
                                                             "user-config"
                                                             "util"
                                                             "workspace"]
                                                :indirect   []}
                                     "common"  {:direct     []
                                                :direct-ifc ["util"]
                                                :indirect   []}
                                     "deps"    {:direct     ["common"]
                                                :direct-ifc ["text-table"
                                                             "util"]
                                                :indirect   []}
                                     "file"    {:direct     []
                                                :direct-ifc ["util"]
                                                :indirect   []}}
          :is-dev                   true
          :is-run-tests             false
          :lib-deps                 {"org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                     "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
          :lib-imports              ["clojure.java.io"
                                     "clojure.pprint"
                                     "clojure.set"
                                     "clojure.string"]
          :lib-imports-test         []
          :lines-of-code-src        0
          :lines-of-code-test       0
          :maven-repos              {"central" {:url "https://repo1.maven.org/maven2/"}}
          :name                     "development"
          :src-paths                ["bases/cli/src"
                                     "components/change/src"
                                     "components/command/src"
                                     "components/common/src"
                                     "components/deps/src"
                                     "components/file/src"]
          :test-base-names          ["cli"]
          :test-component-names     ["change"
                                     "command"]
          :lib-deps-test            {}
          :test-paths               ["bases/cli/test"
                                     "components/change/test"
                                     "components/command/test"
                                     "test"]
          :total-lines-of-code-src  419
          :total-lines-of-code-test 76
          :type                     "project"
          :unmerged                 {:lib-deps      {"org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                                     "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
                                     :src-paths     ["bases/cli/src"
                                                     "components/change/src"
                                                     "components/command/src"
                                                     "components/common/src"
                                                     "components/deps/src"
                                                     "components/file/src"]
                                     :lib-deps-test {}
                                     :test-paths    ["bases/cli/test"
                                                     "components/change/test"
                                                     "components/command/test"
                                                     "test"]}}
         (proj/enrich-project project components bases brick->loc brick->lib-imports
                              {:missing []}
                              {:projects {"development" {:alias "dev"}}}
                              {}))))

(deftest paths--with-active-profile--includes-brick-in-profile
  (is (= {:alias                    "dev"
          :base-names               ["cli"]
          :component-names          ["change"
                                     "command"
                                     "common"
                                     "deps"
                                     "file"
                                     "user"]
          :deps                     {"change"  {:direct     []
                                                :direct-ifc ["git"
                                                             "util"]
                                                :indirect   []}
                                     "cli"     {:direct     ["change"
                                                             "command"
                                                             "file"]
                                                :direct-ifc ["util"
                                                             "workspace"
                                                             "workspace-clj"]
                                                :indirect   ["common"
                                                             "deps"]}
                                     "command" {:direct     ["common"
                                                             "deps"]
                                                :direct-ifc ["creator"
                                                             "help"
                                                             "test-runner"
                                                             "user-config"
                                                             "util"
                                                             "workspace"]
                                                :indirect   []}
                                     "common"  {:direct     []
                                                :direct-ifc ["util"]
                                                :indirect   []}
                                     "deps"    {:direct     ["common"]
                                                :direct-ifc ["text-table"
                                                             "util"]
                                                :indirect   []}
                                     "file"    {:direct     []
                                                :direct-ifc ["util"]
                                                :indirect   []}
                                     "user"    {:direct     []
                                                :direct-ifc []
                                                :indirect   []}}
          :is-dev                   true
          :is-run-tests             true
          :lib-deps                 {"clojure.core.matrix"          "net.mikera/core.matrix"
                                     "org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                     "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
          :lib-imports              ["clojure.java.io"
                                     "clojure.pprint"
                                     "clojure.set"
                                     "clojure.string"]
          :lib-imports-test         []
          :lines-of-code-src        0
          :lines-of-code-test       0
          :maven-repos              {"central" {:url "https://repo1.maven.org/maven2/"}}
          :name                     "development"
          :src-paths                ["bases/cli/src"
                                     "components/change/src"
                                     "components/command/src"
                                     "components/common/src"
                                     "components/deps/src"
                                     "components/file/src"
                                     "components/user/resources"
                                     "components/user/src"]
          :test-base-names          ["cli"]
          :test-component-names     ["change"
                                     "command"
                                     "user"]
          :lib-deps-test            {}
          :test-paths               ["bases/cli/test"
                                     "components/change/test"
                                     "components/command/test"
                                     "components/user/test"
                                     "test"]
          :total-lines-of-code-src  419
          :total-lines-of-code-test 76
          :type                     "project"
          :unmerged                 {:lib-deps      {"org.clojure/clojure"          #:mvn{:version "1.10.1"}
                                                     "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
                                     :src-paths     ["bases/cli/src"
                                                     "components/change/src"
                                                     "components/command/src"
                                                     "components/common/src"
                                                     "components/deps/src"
                                                     "components/file/src"]
                                     :lib-deps-test {}
                                     :test-paths    ["bases/cli/test"
                                                     "components/change/test"
                                                     "components/command/test"
                                                     "test"]}}
         (proj/enrich-project project components bases brick->loc brick->lib-imports
                              {:missing []}
                              {:active-profiles ["default"]
                               :profile-to-settings {"default" {:paths ["components/user/src"
                                                                        "components/user/resources"
                                                                        "components/user/test"]
                                                                :src-paths ["components/user/src"
                                                                            "components/user/resources"]
                                                                :test-paths ["components/user/test"]
                                                                :lib-deps {"clojure.core.matrix"
                                                                           "net.mikera/core.matrix"}}}
                               :projects {"development" {:alias "dev", :test []}}}
                              {:selected-projects #{"dev"}}))))

(deftest is-run-tests--non-dev-project-no-project-selected--returns-true
  (is (true?
        (proj/run-the-tests? "cli" "cli" false false #{}))))

(deftest is-run-tests--non-dev-project-no-project-selected-run-all-brick-tests--returns-true
  (is (true?
        (proj/run-the-tests? "cli" "cli" false true #{}))))

(deftest is-run-tests--non-dev-project-dev-selected--returns-false
  (is (false?
        (proj/run-the-tests? "cli" "cli" false false #{"dev"}))))

(deftest is-run-tests--non-dev-project-dev-selected-run-all-brick-tests--returns-true
  (is (true?
        (proj/run-the-tests? "cli" "cli" false true #{"dev"}))))

(deftest is-run-tests--dev-project-no-project-selected--returns-false
  (is (false?
        (proj/run-the-tests? "development" "dev" true false #{}))))

(deftest is-run-tests--dev-project-no-project-selected-run-all-brick-tests--returns-false
  (is (false?
        (proj/run-the-tests? "development" "dev" true true #{}))))

(deftest is-run-tests--dev-project-dev-selected--returns-true
  (is (true?
        (proj/run-the-tests? "development" "dev" true false #{"dev"}))))

(deftest is-run-tests--dev-project-dev-selected-run-all-brick-tests--returns-true
  (is (true?
        (proj/run-the-tests? "development" "dev" true true #{"dev"}))))
