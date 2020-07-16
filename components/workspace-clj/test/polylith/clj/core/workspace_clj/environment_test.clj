(ns polylith.clj.core.workspace-clj.environment-test
  (:require [clojure.test :refer :all]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.workspace-clj.environment-from-disk :as env-from-disk]))

(def paths ["../../bases/tool/src"
            "../../components/change/src"
            "../../components/common/src"
            "../../components/deps/src"
            "../../components/file/src"
            "src"])

(def deps '{org.clojure/clojure {:mvn/version "1.10.1"}
            org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}
            org.jetbrains.kotlin/kotlin-compiler-embeddable {:mvn/version "1.3.72"}})

(def aliases '{:test {:extra-paths ["../../bases/tool/test"
                                    "../../components/change/test"
                                    "../../components/common/test"]
                      :extra-deps  {}}
               :aot     {:extra-paths ["classes"]
                         :main-opts   ["-e" "(compile,'polylith.core.cli.poly)"]}
               :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                         :main-opts  ["-m" "uberdeps.uberjar"]}})

(deftest environments--config-map-with-aliases--returns-environments
  (is (= {:name "core"
          :type "environment"
          :base-names ["tool"]
          :has-src-dir? false
          :has-test-dir? false
          :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}
                        "clojars" {:url "https://repo.clojars.org/"}}
          :namespaces-src []
          :namespaces-test []
          :paths ["../../components/change/src"
                  "../../components/common/src"
                  "../../components/deps/src"
                  "../../components/file/src"]
          :test-paths ["../../components/change/test"]
          :component-names ["change" "common" "deps" "file"]
          :test-component-names ["change"]
          :lib-deps {"org.clojure/clojure" #:mvn{:version "1.10.1"}
                     "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}
                     "org.jetbrains.kotlin/kotlin-compiler-embeddable" #:mvn{:version "1.3.72"}}
          :test-base-names []
          :test-deps {}}
         (env-from-disk/read-environment "core" "" paths deps aliases mvn/standard-repos))))
