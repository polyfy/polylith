(ns polylith.clj.core.workspace.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.workspace.environment :as env])
  (:refer-clojure :exclude [bases]))

(def components [{:name "change"
                  :interface {:name "change"}
                  :interface-deps ["git" "util"]}
                 {:name "command"
                  :interface {:name "command"}
                  :interface-deps ["common" "create" "deps" "help" "test-runner" "user-config" "util" "workspace"]}
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

(def environment {:name "development"
                  :alias "dev"
                  :dev? true
                  :type "environment"
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
                  :test-lib-deps {}
                  :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}})

(def env->alias {"development" "dev"})

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

(def env->brick->deps {"development" {"change" {:directly ["git" "util"], :indirectly ["shell"]}
                                      "util"    {:directly [], :indirectly []}}})

(deftest paths--without-active-profile--returns-expected-map
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:name "development"
            :active? false
            :dev? true
            :alias "dev"
            :type "environment"
            :lines-of-code-src 0
            :lines-of-code-test 0
            :total-lines-of-code-src 419
            :total-lines-of-code-test 76
            :test-component-names ["change" "command"]
            :component-names ["change" "command" "common" "deps" "file"]
            :base-names ["cli"]
            :test-base-names ["cli"]
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
            :profile-src-paths []
            :profile-test-paths []
            :lib-imports ["clojure.java.io" "clojure.pprint" "clojure.set" "clojure.string"]
            :lib-imports-test []
            :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"},
                       "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}
            :deps                     {"change"  {:direct   []
                                                  :indirect []}
                                       "cli"     {:direct   ["change"
                                                             "command"
                                                             "file"]
                                                  :indirect ["common"
                                                             "deps"]}
                                       "command" {:direct   ["common"
                                                             "deps"]
                                                  :indirect []}
                                       "common"  {:direct   []
                                                  :indirect []}
                                       "deps"    {:direct   ["common"]
                                                  :indirect []}
                                       "file"    {:direct   []
                                                  :indirect []}}
            :test-lib-deps {}
            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}}
           (env/enrich-env environment "" components bases brick->loc brick->lib-imports env->alias
                           {} {})))))

(deftest paths--with-active-profile--includes-brick-in-profile
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:active?                  true
            :alias                    "dev"
            :base-names               ["cli"]
            :component-names          ["change"
                                       "command"
                                       "common"
                                       "deps"
                                       "file"
                                       "user"]
            :deps                     {"change"  {:direct   []
                                                  :indirect []}
                                       "cli"     {:direct   ["change"
                                                             "command"
                                                             "file"]
                                                  :indirect ["common"
                                                             "deps"]}
                                       "command" {:direct   ["common"
                                                             "deps"]
                                                  :indirect []}
                                       "common"  {:direct   []
                                                  :indirect []}
                                       "deps"    {:direct   ["common"]
                                                  :indirect []}
                                       "file"    {:direct   []
                                                  :indirect []}}
            :dev?                     true
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
                                       "components/file/src"]
            :test-paths               ["bases/cli/test"
                                       "components/change/test"
                                       "components/command/test"
                                       "test"]
            :profile-src-paths        ["components/user/resources"
                                       "components/user/src"]
            :profile-test-paths       ["components/user/test"]
            :test-base-names          ["cli"]
            :test-component-names     ["change"
                                       "command"
                                       "user"]
            :test-lib-deps            {}
            :total-lines-of-code-src  419
            :total-lines-of-code-test 76
            :type                     "environment"}
           (env/enrich-env environment "" components bases brick->loc brick->lib-imports env->alias
                           {:active-dev-profiles ["default"]
                            :profile->settings {"default" {:paths ["components/user/src"
                                                                    "components/user/resources"
                                                                    "components/user/test"]
                                                           :src-paths ["components/user/src"
                                                                       "components/user/resources"]
                                                           :test-paths ["components/user/test"]
                                                           :lib-deps {"clojure.core.matrix"
                                                                      "net.mikera/core.matrix"}}}}
                           {:selected-environments #{"dev"}})))))

(deftest active?--non-dev-environment-no-env-selected--returns-true
  (is (true?
       (env/active? "cli" "cli" false false #{}))))

(deftest active?--non-dev-environment-no-env-selected-run-all--returns-true
  (is (true?
       (env/active? "cli" "cli" false true #{}))))

(deftest active?--non-dev-environment-dev-selected--returns-false
  (is (false?
       (env/active? "cli" "cli" false false #{"dev"}))))

(deftest active?--non-dev-environment-dev-selected-run-all--returns-true
  (is (true?
       (env/active? "cli" "cli" false true #{"dev"}))))

(deftest active?--dev-environment-no-env-selected--returns-false
  (is (false?
       (env/active? "development" "dev" true false #{}))))

(deftest active?--dev-environment-no-env-selected-run-all--returns-false
  (is (false?
       (env/active? "development" "dev" true true #{}))))

(deftest active?--dev-environment-dev-selected--returns-true
  (is (true?
       (env/active? "development" "dev" true false #{"dev"}))))

(deftest active?--dev-environment-dev-selected-run-all--returns-true
  (is (true?
       (env/active? "development" "dev" true true #{"dev"}))))
