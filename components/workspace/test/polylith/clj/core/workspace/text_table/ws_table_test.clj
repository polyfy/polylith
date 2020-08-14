(ns polylith.clj.core.workspace.text-table.ws-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.ws-table.core :as ws-table]))

(def workspace {:name "polylith"
                :ws-dir "."
                :ws-reader {:name "polylith-clj"
                            :project-url "https://github.com/tengstrand/polylith/tree/core"
                            :reader-version "1.0"
                            :ws-contract-version "1.0"
                            :language "Clojure"
                            :type-position "postfix"
                            :slash "/"
                            :file-extensions [".clj" "cljc"]}
                :settings {:top-namespace "polylith.clj.core"
                           :color-mode "none"
                           :thousand-sep ","
                           :env->alias {"dev" "dev", "cli" "cli", "core" "core"}}
                :interfaces [{:name "workspace-clj", :type "interface"}
                             {:name "test-runner", :type "interface"}
                             {:name "command"
                              :type "interface"
                              :implementing-components ["command"]}
                             {:name "text-table", :type "interface"}
                             {:name "util"
                              :type "interface"
                              :implementing-components ["util"]}
                             {:name "validate"
                              :type "interface"
                              :implementing-components ["validate"]}
                             {:name "shell"
                              :type "interface"
                              :implementing-components ["shell"]}
                             {:name "workspace"
                              :type "interface"
                              :implementing-components ["workspace"]}
                             {:name "git"
                              :type "interface"
                              :implementing-components ["git"]}
                             {:name "deps"
                              :type "interface"
                              :implementing-components ["deps"]}
                             {:name "help"
                              :type "interface"
                              :implementing-components ["help"]}
                             {:name "file"
                              :type "interface"
                              :implementing-components ["file"]}
                             {:name "common"
                              :type "interface"
                              :implementing-components ["common"]}
                             {:name "change"
                              :type "interface"
                              :implementing-components ["change"]}]
                :components [{:name "change"
                              :type "component"
                              :lines-of-code-src 81
                              :lines-of-code-test 25
                              :interface {:name "change"}
                              :lib-imports-src ["clojure.set" "clojure.string"]
                              :lib-imports-test []
                              :interface-deps ["git" "util"]}
                             {:name "command"
                              :type "component"
                              :lines-of-code-src 36
                              :lines-of-code-test 0
                              :interface {:name "command"}
                              :lib-imports-src ["clojure.pprint"]
                              :lib-imports-test []
                              :interface-deps ["common" "help" "test-runner" "util" "workspace"]}
                             {:name "common"
                              :type "component"
                              :lines-of-code-src 158
                              :lines-of-code-test 0
                              :interface {:name "common"}
                              :lib-imports-src ["clojure.java.io" "clojure.string"]
                              :lib-imports-test []
                              :interface-deps ["util"]}
                             {:name "deps"
                              :type "component"
                              :lines-of-code-src 43
                              :lines-of-code-test 51
                              :interface {:name "deps"}
                              :lib-imports-src ["clojure.string"]
                              :lib-imports-test []
                              :interface-deps []}
                             {:name "deps2"
                              :type "component"
                              :lines-of-code-src 25
                              :lines-of-code-test 0,
                              :interface {:name "deps"}
                              :lib-imports-src ["clojure.string"]
                              :lib-imports-test []
                              :interface-deps []}
                             {:name "file"
                              :type "component"
                              :lines-of-code-src 80
                              :lines-of-code-test 0
                              :interface {:name "file"}
                              :lib-imports-src ["clojure.java.io"]
                              :lib-imports-test []
                              :interface-deps []}
                             {:name "git"
                              :type "component"
                              :lines-of-code-src 31
                              :lines-of-code-test 17
                              :interface {:name "git"}
                              :lib-imports-src ["clojure.string"]
                              :lib-imports-test []
                              :interface-deps ["shell"]}
                             {:name "help"
                              :type "component"
                              :lines-of-code-src 129
                              :lines-of-code-test 0
                              :interface {:name "help"}
                              :lib-imports-src ["clojure.string"]
                              :lib-imports-test []
                              :interface-deps ["util"]}
                             {:name "shell"
                              :type "component"
                              :lines-of-code-src 19
                              :lines-of-code-test 0
                              :interface {:name "shell"}
                              :lib-imports-src ["clojure.java.shell"]
                              :lib-imports-test []
                              :interface-deps []}
                             {:name "test-runner"
                              :type "component"
                              :lines-of-code-src 82
                              :lines-of-code-test 0
                              :interface {:name "test-runner"}
                              :lib-imports-src ["clojure.tools.deps.alpha"]
                              :lib-imports-test []
                              :interface-deps ["common" "file" "util"]}
                             {:name "text-table"
                              :type "component"
                              :lines-of-code-src 65
                              :lines-of-code-test 42
                              :interface {:name "text-table"}
                              :lib-imports-src ["clojure.string"]
                              :lib-imports-test ["clojure.string"]
                              :interface-deps ["util"]}
                             {:name "util"
                              :type "component"
                              :lines-of-code-src 157
                              :lines-of-code-test 47
                              :interface {:name "util"}
                              :lib-imports-src ["clojure.string"]
                              :lib-imports-test []
                              :interface-deps []}
                             {:name "validate"
                              :type "component"
                              :lines-of-code-src 1377
                              :lines-of-code-test 744
                              :interface {:name "validate"}
                              :lib-imports-src ["clojure.set" "clojure.string"]
                              :lib-imports-test []
                              :interface-deps ["common" "deps" "util"]}
                             {:name "workspace"
                              :type "component"
                              :lines-of-code-src 387
                              :lines-of-code-test 95
                              :interface {:name "workspace"}
                              :lib-imports-src ["clojure.set" "clojure.string" "clojure.walk"]
                              :lib-imports-test []
                              :interface-deps ["common" "deps" "file" "text-table" "util" "validate"]}
                             {:name "workspace-clj"
                              :type "component"
                              :lines-of-code-src 301
                              :lines-of-code-test 122
                              :interface {:name "workspace-clj"}
                              :lib-imports-src ["clojure.string" "clojure.tools.deps.alpha.util.maven"]
                              :lib-imports-test ["clojure.tools.deps.alpha.util.maven"]
                              :interface-deps ["common" "file" "util"]}]
                :bases [{:name "cli"
                         :type "base"
                         :lines-of-code-src 21
                         :lines-of-code-test 0
                         :lib-imports-src []
                         :lib-imports-test []
                         :interface-deps ["change" "command" "file" "workspace" "workspace-clj"]}]
                :environments [{:name "cli"
                                :alias "cli"
                                :type "environment"
                                :total-lines-of-code-src 1967
                                :total-lines-of-code-test 1143
                                :test-component-names []
                                :component-names ["change"
                                                  "command"
                                                  "common"
                                                  "deps"
                                                  "file"
                                                  "git"
                                                  "help"
                                                  "shell"
                                                  "test-runner"
                                                  "text-table"
                                                  "util"
                                                  "validate"
                                                  "workspace"
                                                  "workspace-clj"]
                                :base-names ["cli"]
                                :test-base-names []
                                :paths ["../../bases/cli/src"
                                        "../../components/change/src"
                                        "../../components/command/src"
                                        "../../components/common/src"
                                        "../../components/deps/src"
                                        "../../components/file/src"
                                        "../../components/git/src"
                                        "../../components/help/src"
                                        "../../components/shell/src"
                                        "../../components/text-table/src"
                                        "../../components/test-runner/src"
                                        "../../components/util/src"
                                        "../../components/validate/src"
                                        "../../components/workspace/src"
                                        "../../components/workspace-clj/src"]
                                :test-paths []
                                :lib-imports ["clojure.java.io"
                                              "clojure.java.shell"
                                              "clojure.pprint"
                                              "clojure.set"
                                              "clojure.string"
                                              "clojure.tools.deps.alpha"
                                              "clojure.tools.deps.alpha.util.maven"
                                              "clojure.walk"],
                                :lib-imports-test ["clojure.string" "clojure.tools.deps.alpha.util.maven"]
                                :lib-deps {"org.clojure/clojure" #:mvn{:version "1.10.1"}
                                           "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
                                :test-lib-deps {}
                                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}
                                              "clojars" {:url "https://repo.clojars.org/"}}}
                               {:name "core"
                                :alias "core"
                                :type "environment"
                                :total-lines-of-code-src 1527
                                :total-lines-of-code-test 1021
                                :test-component-names []
                                :component-names ["change"
                                                  "common"
                                                  "deps2"
                                                  "file"
                                                  "git"
                                                  "help"
                                                  "shell"
                                                  "text-table"
                                                  "util"
                                                  "validate"
                                                  "workspace"]
                                :base-names []
                                :test-base-names []
                                :paths ["../../components/change/src"
                                        "../../components/common/src"
                                        "../../components/deps2/src"
                                        "../../components/file/src"
                                        "../../components/git/src"
                                        "../../components/help/src"
                                        "../../components/shell/src"
                                        "../../components/text-table/src"
                                        "../../components/util/src"
                                        "../../components/validate/src"
                                        "../../components/workspace/src"]
                                :test-paths []
                                :lib-imports ["clojure.java.io" "clojure.java.shell" "clojure.set" "clojure.string" "clojure.walk"]
                                :lib-imports-test ["clojure.string"]
                                :lib-deps {"org.clojure/clojure" #:mvn{:version "1.10.1"}
                                           "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}}
                                :test-lib-deps {}
                                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}
                                              "clojars" {:url "https://repo.clojars.org/"}}}
                               {:name "dev"
                                :alias "dev"
                                :type "environment"
                                :total-lines-of-code-src 2020
                                :total-lines-of-code-test 1143
                                :test-component-names ["change"
                                                       "command"
                                                       "common"
                                                       "deps"
                                                       "file"
                                                       "git"
                                                       "help"
                                                       "shell"
                                                       "test-runner"
                                                       "text-table"
                                                       "util"
                                                       "validate"
                                                       "workspace"
                                                       "workspace-clj"]
                                :component-names ["change"
                                                  "command"
                                                  "common"
                                                  "deps"
                                                  "file"
                                                  "git"
                                                  "help"
                                                  "shell"
                                                  "test-runner"
                                                  "text-table"
                                                  "util"
                                                  "validate"
                                                  "workspace"
                                                  "workspace-clj"]
                                :base-names ["cli"]
                                :test-base-names ["cli"]
                                :paths ["../../bases/cli/src"
                                        "../../components/change/src"
                                        "../../components/command/src"
                                        "../../components/common/src"
                                        "../../components/file/src"
                                        "../../components/git/src"
                                        "../../components/help/src"
                                        "../../components/shell/src"
                                        "../../components/test-runner/src"
                                        "../../components/text-table/src"
                                        "../../components/util/src"
                                        "../../components/validate/src"
                                        "../../components/workspace/src"
                                        "../../components/workspace-clj/src"]
                                :test-paths ["../../bases/cli/test"
                                             "../../components/change/test"
                                             "../../components/command/test"
                                             "../../components/common/test"
                                             "../../components/file/test"
                                             "../../components/git/test"
                                             "../../components/help/test"
                                             "../../components/shell/test"
                                             "../../components/test-runner/test"
                                             "../../components/text-table/test"
                                             "../../components/util/test"
                                             "../../components/validate/test"
                                             "../../components/workspace-clj/test"
                                             "../../components/workspace/test"]
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
                                :test-lib-deps {}
                                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"}
                                              "clojars" {:url "https://repo.clojars.org/"}}}]
                :total-loc-src-bricks 3020
                :total-loc-test-bricks 2143
                :changes {:changed-components ["help" "text-table" "util" "workspace"]
                          :changed-bases ["cli"]
                          :env->bricks-to-test {"cli"  []
                                                "core" []
                                                "dev"  ["file" "cli"]}}
                :messages []})

(def workspace-with-profiles (-> workspace
                                 (assoc-in [:settings :active-dev-profiles] #{:default})
                                 (assoc-in [:settings :profile->settings] {:default {:src-bricks #{"deps"}
                                                                                     :test-bricks #{"deps"}}
                                                                           :adm {:src-bricks #{"deps2"}
                                                                                 :test-bricks #{"deps2"}}})))


(deftest ws-table--without-loc-info--return-table-without-loc-info
  (is (= ["  interface      brick          cli  core  dev"
          "  --------------------------------------------"
          "  change         change         x--  x--   xx-"
          "  command        command        x--  ---   xx-"
          "  common         common         x--  x--   xx-"
          "  deps           deps           x--  ---   xx-"
          "  deps           deps2          ---  x--   ---"
          "  file           file           x--  x--   xxx"
          "  git            git            x--  x--   xx-"
          "  help           help *         x--  x--   xx-"
          "  shell          shell          x--  x--   xx-"
          "  test-runner    test-runner    x--  ---   xx-"
          "  text-table     text-table *   x--  x--   xx-"
          "  util           util *         x--  x--   xx-"
          "  validate       validate       x--  x--   xx-"
          "  workspace      workspace *    x--  x--   xx-"
          "  workspace-clj  workspace-clj  x--  ---   xx-"
          "  -              cli *          x--  ---   xxx"]
         (ws-table/table workspace false))))

(deftest ws-table--with-loc-info--return-table-with-loc-info
  (is (= ["  interface      brick           cli   core    dev     loc   (t)"
          "  --------------------------------------------------------------"
          "  change         change          x--    x--    xx-      81    25"
          "  command        command         x--    ---    xx-      36     0"
          "  common         common          x--    x--    xx-     158     0"
          "  deps           deps            x--    ---    xx-      43    51"
          "  deps           deps2           ---    x--    ---      25     0"
          "  file           file            x--    x--    xxx      80     0"
          "  git            git             x--    x--    xx-      31    17"
          "  help           help *          x--    x--    xx-     129     0"
          "  shell          shell           x--    x--    xx-      19     0"
          "  test-runner    test-runner     x--    ---    xx-      82     0"
          "  text-table     text-table *    x--    x--    xx-      65    42"
          "  util           util *          x--    x--    xx-     157    47"
          "  validate       validate        x--    x--    xx-   1,377   744"
          "  workspace      workspace *     x--    x--    xx-     387    95"
          "  workspace-clj  workspace-clj   x--    ---    xx-     301   122"
          "  -              cli *           x--    ---    xxx      21     0"
          "                                1,967  1,527  2,020  3,020 2,143"]
         (ws-table/table workspace true))))

(deftest ws-table--with-profiles-without-loc-info--return-table-without-loc-info
  (is (= ["  interface      brick          cli  core  dev  adm"
          "  -------------------------------------------------"
          "  change         change         x--  x--   xx-  -- "
          "  command        command        x--  ---   xx-  -- "
          "  common         common         x--  x--   xx-  -- "
          "  deps           deps           x--  ---   xx-  -- "
          "  deps           deps2          ---  x--   ---  xx "
          "  file           file           x--  x--   xxx  -- "
          "  git            git            x--  x--   xx-  -- "
          "  help           help *         x--  x--   xx-  -- "
          "  shell          shell          x--  x--   xx-  -- "
          "  test-runner    test-runner    x--  ---   xx-  -- "
          "  text-table     text-table *   x--  x--   xx-  -- "
          "  util           util *         x--  x--   xx-  -- "
          "  validate       validate       x--  x--   xx-  -- "
          "  workspace      workspace *    x--  x--   xx-  -- "
          "  workspace-clj  workspace-clj  x--  ---   xx-  -- "
          "  -              cli *          x--  ---   xxx  -- "]
         (ws-table/table workspace-with-profiles false))))

(deftest ws-table--with-profiles-with-loc-info--return-table-without-loc-info
  (is (= ["  interface      brick           cli   core    dev   adm    loc   (t)"
          "  -------------------------------------------------------------------"
          "  change         change          x--    x--    xx-   --      81    25"
          "  command        command         x--    ---    xx-   --      36     0"
          "  common         common          x--    x--    xx-   --     158     0"
          "  deps           deps            x--    ---    xx-   --      43    51"
          "  deps           deps2           ---    x--    ---   xx      25     0"
          "  file           file            x--    x--    xxx   --      80     0"
          "  git            git             x--    x--    xx-   --      31    17"
          "  help           help *          x--    x--    xx-   --     129     0"
          "  shell          shell           x--    x--    xx-   --      19     0"
          "  test-runner    test-runner     x--    ---    xx-   --      82     0"
          "  text-table     text-table *    x--    x--    xx-   --      65    42"
          "  util           util *          x--    x--    xx-   --     157    47"
          "  validate       validate        x--    x--    xx-   --   1,377   744"
          "  workspace      workspace *     x--    x--    xx-   --     387    95"
          "  workspace-clj  workspace-clj   x--    ---    xx-   --     301   122"
          "  -              cli *           x--    ---    xxx   --      21     0"
          "                                1,967  1,527  2,020       3,020 2,143"]
         (ws-table/table workspace-with-profiles true))))
