(ns polylith.clj.core.workspace.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interfc :as file]
            [polylith.clj.core.workspace.environment :as env]))

(def environment {:name "development"
                  :alias "dev"
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
                  :test-deps {}
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

(deftest paths--without-active-profile--returns-excpected-map
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:name "development"
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
            :lib-imports ["clojure.java.io" "clojure.pprint" "clojure.set" "clojure.string"]
            :lib-imports-test []
            :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"},
                       "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}}
            :deps {"change" {:directly ["git" "util"], :indirectly ["shell"]}
                   "util" {:directly [], :indirectly []}}
            :test-deps {}
            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}}
           (env/enrich-env environment "" brick->loc brick->lib-imports env->alias env->brick->deps
                           [] {})))))


(deftest paths--with-active-profile--includes-brick-in-profile
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:name "development"
            :alias "dev"
            :type "environment"
            :lines-of-code-src 0
            :lines-of-code-test 0
            :total-lines-of-code-src 419
            :total-lines-of-code-test 76
            :test-component-names ["change" "command" "user"]
            :component-names ["change" "command" "common" "deps" "file" "user"]
            :base-names ["cli"]
            :test-base-names ["cli"]
            :src-paths ["bases/cli/src"
                        "components/change/src"
                        "components/command/src"
                        "components/common/src"
                        "components/deps/src"
                        "components/file/src"
                        "components/user/resources"
                        "components/user/src"]
            :test-paths ["bases/cli/test"
                         "components/change/test"
                         "components/command/test"
                         "components/user/test"
                         "test"]
            :lib-imports ["clojure.java.io" "clojure.pprint" "clojure.set" "clojure.string"]
            :lib-imports-test []
            :lib-deps {"org.clojure/clojure" {:mvn/version "1.10.1"},
                       "org.clojure/tools.deps.alpha" {:mvn/version "0.8.695"}
                       "clojure.core.matrix"          "net.mikera/core.matrix"}
            :deps {"change" {:directly ["git" "util"], :indirectly ["shell"]}
                   "util" {:directly [], :indirectly []}}
            :test-deps {}
            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}}
           (env/enrich-env environment "" brick->loc brick->lib-imports env->alias env->brick->deps
                           [:default] {:default {:paths ["components/user/src"
                                                         "components/user/resources"
                                                         "components/user/test"]
                                                 :deps {"clojure.core.matrix" "net.mikera/core.matrix"}}})))))
