(ns polylith.clj.core.workspace-clj.project-test
  (:require [clojure.test :refer :all]
            [clojure.tools.deps.alpha.util.maven :as mvn]
            [polylith.clj.core.workspace-clj.projects-from-disk :as projs]))

(def paths ["../../bases/tool/src"
            "../../components/change/src"
            "../../components/common/src"
            "../../components/deps/src"
            "../../components/file/src"
            "src"])

(def deps '{org.clojure/clojure {:mvn/version "1.10.1"}
            org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}})

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
  (is (= "projects/core/test"
         (projs/absolute-path "test" "core"))))

(deftest clean-path--given-a-local-path-with-dot-syntax--return-workspace-path
  (is (= "projects/core/test"
         (projs/absolute-path "./test" "core"))))

(deftest clean-path--given-a-relative-path--return-root-path
  (is (= "components/comp"
         (projs/absolute-path "../../components/comp" "dev"))))

(defn filter-version [[library {:keys [version]}]]
  [library {:version version}])

(deftest projects--config-map-with-aliases--returns-projects
  (let [result (projs/read-project "poly" "projects/poly" "projects/poly/deps.edn" false paths deps aliases mvn/standard-repos "/USER")]
    (is (= {:config-file     "projects/poly/deps.edn"
            :is-dev          false
            :project-dir     "projects/poly"
            :maven-repos     {"central" {:url "https://repo1.maven.org/maven2/"}
                              "clojars" {:url "https://repo.clojars.org/"}}
            :name            "poly"
            :namespaces-src  []
            :namespaces-test [{:file-path "projects/poly/test/project/dummy.clj"
                               :imports   ["clojure.test"]
                               :name      "project.dummy"
                               :namespace "project.dummy"}]
            :src-paths       ["bases/tool/src"
                              "components/change/src"
                              "components/common/src"
                              "components/deps/src"
                              "components/file/src"
                              "projects/poly/src"]
            :lib-deps-test   {}
            :test-paths      ["bases/tool/test"
                              "components/change/test"
                              "components/common/test"]
            :type            "project"}
           (dissoc result :lib-deps)))
    (is (= {"org.clojure/clojure"                             {:type    "maven"
                                                               :version "1.10.1"
                                                               :size    3908431}
            "org.clojure/tools.deps.alpha"                    {:type    "maven"
                                                               :version "0.8.695"
                                                               :size    47566}}
           (:lib-deps result)))))
