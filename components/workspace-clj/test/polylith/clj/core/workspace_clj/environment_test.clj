(ns polylith.clj.core.workspace-clj.environment-test
  (:require [clojure.test :refer :all]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.file.interfc :as file]
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
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:name "core"
            :type "environment"
            :env-dir "environments/core"
            :config-file "environments/core/deps.edn"
            :has-src-dir? true
            :has-test-dir? false
            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}
                          "clojars" {:url "https://repo.clojars.org/"}}
            :namespaces-src []
            :namespaces-test [{:file-path "environments/core/test/polylith/clj/core/dev_test.clj"
                               :imports   []
                               :name      "polylith.clj.core.dev-test"
                               :namespace "polylith.clj.core.dev-test"}]
            :src-paths ["bases/tool/src"
                        "components/change/src"
                        "components/common/src"
                        "components/deps/src"
                        "components/file/src"
                        "environments/core/src"]
            :test-paths ["bases/tool/test"
                         "components/change/test"
                         "components/common/test"]
            :lib-deps {"org.clojure/clojure" #:mvn{:version "1.10.1"}
                       "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}
                       "org.jetbrains.kotlin/kotlin-compiler-embeddable" #:mvn{:version "1.3.72"}}
            :test-deps {}}
           (env/read-environment "core" "environments/core" "environments/core/deps.edn" false paths deps aliases mvn/standard-repos)))))
