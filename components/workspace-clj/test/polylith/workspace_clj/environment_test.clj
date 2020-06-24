(ns polylith.workspace-clj.environment-test
  (:require [clojure.test :refer :all]
            [polylith.workspace-clj.environment :as env]))

(def paths [; Bases
            "../../bases/tool/src"

            ; Components
            "../../components/change/src"
            "../../components/common/src"
            "../../components/deps/src"
            "../../components/file/src"
            "../../components/git/src"
            "../../components/shell/src"
            "../../components/spec/src"
            "../../components/test/src"
            "../../components/util/src"
            "../../components/validate/src"
            "../../components/workspace/src"
            "../../components/workspace-clj/src"
            "../../components/workspace-kotlin/src"])

(def deps '{org.clojure/clojure {:mvn/version "1.10.1"}
            org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}
            org.jetbrains.kotlin/kotlin-compiler-embeddable {:mvn/version "1.3.72"}})

(def aliases '{:test {:extra-paths [;Base
                                    "../../bases/tool/test"

                                    ; Components
                                    "../../components/change/test"
                                    "../../components/common/test"
                                    "../../components/deps/test"
                                    "../../components/file/test"
                                    "../../components/git/test"
                                    "../../components/shell/test"
                                    "../../components/spec/test"
                                    "../../components/test/test"
                                    "../../components/util/test"
                                    "../../components/validate/test"
                                    "../../components/workspace/test"
                                    "../../components/workspace-clj/test"
                                    "../../components/workspace-kotlin/test"]
                      :extra-deps  {}}

               :aot     {:extra-paths ["classes"]
                         :main-opts   ["-e" "(compile,'polylith.cli.poly)"]}

               :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                         :main-opts  ["-m" "uberdeps.uberjar"]}})

(def maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}
                  "clojars" {:url "https://clojars.org/repo"}})

(deftest environments--config-map-with-aliases--returns-environments
  (is (= [{:name            "core"
           :group           "core"
           :test?           false
           :type            "environment"
           :component-names ["change"
                             "common"
                             "deps"
                             "file"
                             "git"
                             "shell"
                             "spec"
                             "test"
                             "util"
                             "validate"
                             "workspace"
                             "workspace-clj"
                             "workspace-kotlin"]
           :base-names      ["tool"]
           :deps            {"org.clojure/clojure"                             #:mvn{:version "1.10.1"}
                             "org.clojure/tools.deps.alpha"                    #:mvn{:version "0.8.695"}
                             "org.jetbrains.kotlin/kotlin-compiler-embeddable" #:mvn{:version "1.3.72"}}
           :paths           ["../../bases/tool/src"
                             "../../components/change/src"
                             "../../components/common/src"
                             "../../components/deps/src"
                             "../../components/file/src"
                             "../../components/git/src"
                             "../../components/shell/src"
                             "../../components/spec/src"
                             "../../components/test/src"
                             "../../components/util/src"
                             "../../components/validate/src"
                             "../../components/workspace/src"
                             "../../components/workspace-clj/src"
                             "../../components/workspace-kotlin/src"]
           :maven-repos     {"central" {:url "https://repo1.maven.org/maven2/"}
                             "clojars" {:url "https://clojars.org/repo"}}}
          {
           :name            "core-test"
           :group           "core"
           :test?           true
           :type            "environment"
           :component-names ["change"
                             "common"
                             "deps"
                             "file"
                             "git"
                             "shell"
                             "spec"
                             "test"
                             "util"
                             "validate"
                             "workspace"
                             "workspace-clj"
                             "workspace-kotlin"]
           :base-names      ["tool"]
           :deps            {"org.clojure/clojure"                             #:mvn{:version "1.10.1"}
                             "org.clojure/tools.deps.alpha"                    #:mvn{:version "0.8.695"}
                             "org.jetbrains.kotlin/kotlin-compiler-embeddable" #:mvn{:version "1.3.72"}}
           :paths           ["../../bases/tool/src"
                             "../../bases/tool/test"
                             "../../components/change/src"
                             "../../components/change/test"
                             "../../components/common/src"
                             "../../components/common/test"
                             "../../components/deps/src"
                             "../../components/deps/test"
                             "../../components/file/src"
                             "../../components/file/test"
                             "../../components/git/src"
                             "../../components/git/test"
                             "../../components/shell/src"
                             "../../components/shell/test"
                             "../../components/spec/src"
                             "../../components/spec/test"
                             "../../components/test/src"
                             "../../components/test/test"
                             "../../components/util/src"
                             "../../components/util/test"
                             "../../components/validate/src"
                             "../../components/validate/test"
                             "../../components/workspace-clj/src"
                             "../../components/workspace-clj/test"
                             "../../components/workspace-kotlin/src"
                             "../../components/workspace-kotlin/test"
                             "../../components/workspace/src"
                             "../../components/workspace/test"]
           :maven-repos     {"central" {:url "https://repo1.maven.org/maven2/"}
                             "clojars" {:url "https://clojars.org/repo"}}}]
         (env/environment "core" paths deps aliases maven-repos))))
