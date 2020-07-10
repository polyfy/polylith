(ns polylith.clj.core.workspace.environment-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.environment :as env]))

(def environment {:name "development"
                  :alias "dev"
                  :type "environment"
                  :lines-of-code-src 3199
                  :lines-of-code-test 1628
                  :test-component-names ["change" "command"]
                  :component-names ["change" "command" "common" "deps" "file"]
                  :base-names ["cli"]
                  :test-base-names ["cli"]
                  :paths ["../../bases/cli/src"
                          "../../bases/z-jocke/src"
                          "../../components/change/src"
                          "../../components/command/src"
                          "../../components/common/src"
                          "../../components/deps/src"
                          "../../components/file/src"]
                  :test-paths ["../../bases/cli/test"
                               "../../components/change/test"
                               "../../components/command/test"
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
                  :deps {"org.clojure/clojure" #:mvn{:version "1.10.1"}
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

(deftest clean-path--given-a-local-path--return-workspace-path
  (is (= "environments/dev/test"
         (env/ws-root-path "test" "dev"))))

(deftest clean-path--given-a-local-path-with-dot-syntax--return-workspace-path
  (is (= "environments/dev/test"
         (env/ws-root-path "./test" "dev"))))

(deftest clean-path--given-a-relative-path--return-root-path
  (is (= "components/comp"
         (env/ws-root-path "../../components/comp" "dev"))))

(deftest paths--when-include-test-path-flag-is-false---include-only-src-paths
  (is (= {:name "development"
          :alias "dev"
          :type "environment"
          :lines-of-code-src 419
          :lines-of-code-test 76
          :test-component-names ["change" "command"]
          :component-names ["change" "command" "common" "deps" "file"]
          :base-names ["cli"]
          :test-base-names ["cli"]
          :paths ["bases/cli/src"
                  "bases/z-jocke/src"
                  "components/change/src"
                  "components/command/src"
                  "components/common/src"
                  "components/deps/src"
                  "components/file/src"],
          :test-paths ["bases/cli/test"
                       "components/change/test"
                       "components/command/test"
                       "environments/development/test"]
          :lib-imports ["clojure.java.io" "clojure.pprint" "clojure.set" "clojure.string"]
          :lib-imports-test []
          :deps {"org.clojure/clojure" #:mvn{:version "1.10.1"},
                 "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
          :test-deps {}
          :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}}}
         (env/enrich-env environment brick->loc brick->lib-imports env->alias))))
