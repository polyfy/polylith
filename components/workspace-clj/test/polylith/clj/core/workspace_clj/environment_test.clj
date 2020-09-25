(ns polylith.clj.core.workspace-clj.environment-test
  (:require [clojure.test :refer :all]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.workspace-clj.environment-from-disk :as env]))

(def paths ["../../bases/tool/src"
            "../../components/change/src"
            "../../components/common/src"
            "../../components/deps/src"
            "../../components/file/src"
            "src"])

(def deps '{org.clojure/clojure {:mvn/version "1.10.1"}
            org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}
            org.jetbrains.kotlin/kotlin-compiler-embeddable {:mvn/version "1.3.72"}})

(def aliases '{:dev {:extra-paths paths
                     :extra-deps deps}

               :test {:extra-paths ["../../bases/tool/test"
                                    "../../components/change/test"
                                    "../../components/common/test"]
                      :extra-deps  {}}
               :aot     {:extra-paths ["classes"]
                         :main-opts   ["-e" "(compile,'polylith.core.cli.poly)"]}
               :uberjar {:extra-deps {uberdeps {:mvn/version "0.1.10"}}
                         :main-opts  ["-m" "uberdeps.uberjar"]}})

(deftest clean-path--given-a-local-path--return-workspace-path
  (is (= "environments/dev/test"
         (env/absolute-path "test" "dev"))))

(deftest clean-path--given-a-local-path-with-dot-syntax--return-workspace-path
  (is (= "environments/dev/test"
         (env/absolute-path "./test" "dev"))))

(deftest clean-path--given-a-relative-path--return-root-path
  (is (= "components/comp"
         (env/absolute-path "../../components/comp" "dev"))))

(deftest environments--config-map-with-aliases--returns-environments
  (is (= {:config-file     "environments/poly/deps.edn"
          :dev?            false
          :env-dir         "environments/poly"
          :lib-deps        {"org.clojure/clojure"                             {:mvn/version "1.10.1"
                                                                               :size        3908431}
                            "org.clojure/tools.deps.alpha"                    {:mvn/version "0.8.695"
                                                                               :size        47566}
                            "org.jetbrains.kotlin/kotlin-compiler-embeddable" {:mvn/version "1.3.72"
                                                                               :size        40201239}}
          :maven-repos     {"central" {:url "https://repo1.maven.org/maven2/"}
                            "clojars" {:url "https://repo.clojars.org/"}}
          :name            "poly"
          :namespaces-src  []
          :namespaces-test [{:file-path "environments/poly/test/env/dummy.clj"
                             :imports   ["clojure.test"]
                             :name      "env.dummy"
                             :namespace "env.dummy"}]
          :src-paths       ["bases/tool/src"
                            "components/change/src"
                            "components/common/src"
                            "components/deps/src"
                            "components/file/src"
                            "environments/poly/src"]
          :test-lib-deps   {}
          :test-paths      ["bases/tool/test"
                            "components/change/test"
                            "components/common/test"]
          :type            "environment"}
         (env/read-environment "poly" "environments/poly" "environments/poly/deps.edn" false paths deps aliases mvn/standard-repos))))
