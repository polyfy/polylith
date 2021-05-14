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

(def test-paths ["../../bases/tool/test"
                 "../../components/change/test"
                 "../../components/common/test"])

(def src-deps '{org.clojure/clojure      {:mvn/version "1.10.1"}
                org.clojure/tools.deps.alpha {:mvn/version "0.8.695"}})

(def test-deps {})

(def project {:project-name "poly"
              :poject-dir "./projects/poly"
              :project-config-dir "./projects/poly"
              :is-dev false})

(deftest clean-path--given-a-local-path--return-workspace-path
  (is (= "projects/core/test"
         (projs/absolute-path "test" "core" false))))

(deftest clean-path--given-a-local-path-with-dot-syntax--return-workspace-path
  (is (= "projects/core/test"
         (projs/absolute-path "./test" "core" false))))

(deftest clean-path--given-a-relative-path--return-root-path
  (is (= "components/comp"
         (projs/absolute-path "components/comp" "dev" true))))

(defn filter-version [[library {:keys [version]}]]
  [library {:version version}])

(deftest projects--config-map-with-aliases--returns-projects
  (is (= {:name            "poly"
          :type            "project"
          :is-dev          false
          :config-filename     "./projects/poly/deps.edn"
          :lib-deps        {:src {"me.raynes/fs"                              {:size 11209, :type "maven", :version "1.4.6"}
                                  "metosin/malli"                             {:size 28211, :type "maven", :version "0.1.0"}
                                  "mvxcvi/puget"                              {:size 15930, :type "maven", :version "1.3.1"}
                                  "org.apache.logging.log4j/log4j-core"       {:size 1714164, :type "maven", :version "2.13.3"}
                                  "org.apache.logging.log4j/log4j-slf4j-impl" {:size 23590, :type "maven", :version "2.13.3"}
                                  "org.clojure/clojure"                       {:size 3908431, :type "maven", :version "1.10.1"}
                                  "org.clojure/tools.deps.alpha"              {:size 47566, :type "maven", :version "0.8.695"}}}
          :maven-repos     {"central" {:url "https://repo1.maven.org/maven2/"}
                            "clojars" {:url "https://repo.clojars.org/"}}
          :namespaces {}
          :paths      {:src ["bases/poly-cli/src"
                             "components/change/src"
                             "components/command/src"
                             "components/common/src"
                             "components/creator/resources"
                             "components/creator/src"
                             "components/deps/src"
                             "components/file/src"
                             "components/git/src"
                             "components/help/src"
                             "components/lib/src"
                             "components/path-finder/src"
                             "components/shell/src"
                             "components/test-runner/src"
                             "components/text-table/src"
                             "components/user-config/src"
                             "components/user-input/src"
                             "components/util/src"
                             "components/validator/src"
                             "components/version/src"
                             "components/workspace-clj/src"
                             "components/workspace/src"
                             "components/ws-explorer/src"]
                       :test ["components/change/test"
                              "components/command/test"
                              "components/creator/test"
                              "components/deps/test"
                              "components/file/test"
                              "components/git/test"
                              "components/lib/test"
                              "components/path-finder/test"
                              "components/test-helper/src"
                              "components/test-runner/test"
                              "components/user-input/test"
                              "components/util/test"
                              "components/validator/test"
                              "components/workspace-clj/test"
                              "components/workspace/test"
                              "components/ws-explorer/test"
                              "projects/poly/test"]}}
         (projs/read-project project :toolsdeps2 {} "/USER" "none"))))
