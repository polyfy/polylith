(ns polylith.clj.core.change.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.core :as core]))

(def files ["components/change/test/polylith/clj/core/change/brick_test.clj"
            "components/change/test/polylith/clj/core/change/core_test.clj"
            "components/deps/src/polylith/clj/core/deps/interface.clj"])

(def projects [{:name                 "cli"
                :is-run-tests         true
                :is-dev               false
                :paths {}
                :component-names      {:src ["change"
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
                                             "validator"
                                             "workspace"
                                             "workspace-clj"]}
                :base-names           {:src ["cli"]}
                :deps                 {"workspace-clj" {:src {:direct ["common" "file" "util"], :indirect []}}
                                       "test-runner" {:src {:direct ["common" "util"], :indirect []}}
                                       "command" {:src {:direct ["common" "help" "test-runner" "util" "workspace"]
                                                        :indirect ["deps" "file" "text-table" "validator"]}}
                                       "text-table" {:src {:direct ["util"], :indirect []}}
                                       "util" {:src {:direct [], :indirect []}}
                                       "validator" {:src {:direct ["common" "deps" "util"], :indirect []}}
                                       "shell" {:src {:direct [], :indirect []}}
                                       "workspace" {:src {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}}
                                       "cli" {:src {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                                                    :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]}}
                                       "git" {:src {:direct ["shell"], :indirect []}}
                                       "deps" {:src {:direct [], :indirect []}}
                                       "help" {:src {:direct ["util"], :indirect []}}
                                       "file" {:src {:direct [], :indirect []}}
                                       "common" {:src {:direct ["util"], :indirect []}}
                                       "change" {:src {:direct ["git" "util"], :indirect ["shell"]}}}}
               {:name "core"
                :is-run-tests true
                :is-dev false
                :component-names {:src ["change" "common" "deps" "file" "git" "help" "shell" "text-table" "util" "validator" "workspace"]
                                  :test ["change" "common" "deps" "file" "git" "help" "shell"]}
                :base-names {}
                :paths {:src ["components/change/src"
                              "components/common/src"
                              "components/deps/src"
                              "components/file/src"
                              "components/git/src"
                              "components/help/src"
                              "components/shell/src"
                              "components/text-table/src"
                              "components/util/src"
                              "components/validator/src"
                              "components/workspace/src"]}
                :deps {"workspace-clj" {:src {:direct ["common" "file" "util"], :indirect []}}
                       "test-runner" {:src {:direct ["common" "util"], :indirect []}}
                       "command" {:src {:direct ["common" "help" "util" "workspace"], :indirect ["deps" "file" "text-table" "validator"]}}
                       "text-table" {:src {:direct ["util"], :indirect []}}
                       "util" {:src {:direct [], :indirect []}}
                       "validator" {:src {:direct ["common" "deps" "util"], :indirect []}}
                       "shell" {:src {:direct [], :indirect []}}
                       "workspace" {:src {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}}
                       "cli" {:src {:direct ["change" "file" "workspace"]
                                    :indirect ["common" "deps" "git" "shell" "text-table" "util" "validator"]}}
                       "git" {:src {:direct ["shell"], :indirect []}}
                       "deps" {:src {:direct [], :indirect []}}
                       "help" {:src {:direct ["util"], :indirect []}}
                       "file" {:src {:direct [], :indirect []}}
                       "common" {:src {:direct ["util"], :indirect []}}
                       "change" {:src {:direct ["git" "util"], :indirect ["shell"]}}}}
               {:name "dev"
                :is-run-tests false
                :is-dev true
                :component-names {:src ["change"
                                        "command"
                                        "shell"]
                                  :test ["change" "common" "shell"]}
                :base-names {:src ["cli"], :test ["cli"]}
                :paths {:src ["bases/cli/src"
                              "components/change/src"
                              "components/command/src"
                              "components/common/src"
                              "components/deps/src"
                              "components/file/src"
                              "components/git/src"
                              "components/help/src"
                              "components/shell/src"
                              "components/test-runner/src"
                              "components/text-table/src"
                              "components/util/src"
                              "components/validator/src"
                              "components/workspace/src"
                              "components/workspace-clj/src"]}
                :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
                       "test-runner" {:direct ["common" "util"], :indirect []}
                       "command" {:direct ["common" "help" "test-runner" "util" "workspace"]
                                  :indirect ["deps" "file" "text-table" "validator"]}
                       "text-table" {:direct ["util"], :indirect []}
                       "util" {:direct [], :indirect []}
                       "validator" {:direct ["common" "deps" "util"], :indirect []}
                       "shell" {:direct [], :indirect []}
                       "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}
                       "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                              :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validator"]}
                       "git" {:direct ["shell"], :indirect []}
                       "deps" {:direct [], :indirect []}
                       "help" {:direct ["util"], :indirect []}
                       "file" {:direct [], :indirect []}
                       "common" {:direct ["util"], :indirect []}
                       "change" {:direct ["git" "util"], :indirect ["shell"]}}
                :deps-test {"workspace-clj" {:direct [], :indirect []}
                            "test-runner" {:direct [], :indirect []}
                            "command" {:direct [], :indirect []}
                            "text-table" {:direct [], :indirect []}
                            "util" {:direct [], :indirect []}
                            "validator" {:direct [], :indirect []}
                            "shell" {:direct [], :indirect []}
                            "workspace" {:direct [], :indirect []}
                            "cli" {:direct [], :indirect []}
                            "git" {:direct [], :indirect []}
                            "deps" {:direct [], :indirect []}
                            "help" {:direct [], :indirect []}
                            "file" {:direct [], :indirect []}
                            "common" {:direct [], :indirect []}
                            "change" {:direct [], :indirect []}}}])

(def workspace {:projects projects
                :settings {}
                :user-input   {}
                :paths        {:missing []}})

(def workspace-with-active-dev (assoc-in workspace [:projects 2 :is-run-tests] true))

(def workspace-with-run-all-brick-tests-flags (assoc workspace :user-input {:is-run-all-brick-tests true
                                                                            :is-run-project-tests true}))

(deftest changes--a-list-of-changed-files-and-projects--returns-changed-bricks-and-bricks-to-test
  (is (= {:changed-bases                []
          :changed-components           ["change"
                                         "deps"]
          :changed-files                ["components/change/test/polylith/clj/core/change/brick_test.clj"
                                         "components/change/test/polylith/clj/core/change/core_test.clj"
                                         "components/deps/src/polylith/clj/core/deps/interface.clj"]
          :changed-or-affected-projects ["cli"
                                         "core"
                                         "dev"]
          :changed-projects             []
          :git-command                  "git diff --name-only"
          :project-to-bricks-to-test    {"cli"  []
                                         "core" ["change"
                                                 "deps"]
                                         "dev"  []}
          :project-to-indirect-changes  {"cli"  {:src  ["cli"
                                                        "command"
                                                        "validator"
                                                        "workspace"]
                                                 :test []}
                                         "core" {:src  ["cli"
                                                        "command"
                                                        "validator"
                                                        "workspace"]
                                                 :test []}
                                         "dev"  {:src  []
                                                 :test []}}
          :project-to-projects-to-test  {"cli"  []
                                         "core" []
                                         "dev"  []}}
         (core/changes workspace {:files files} nil))))

(deftest changes--a-list-of-changed-files-and-active-dev--returns-changed-bricks-and-bricks-to-test
  (is (= {:changed-bases                []
          :changed-components           ["change"
                                         "deps"]
          :changed-files                ["components/change/test/polylith/clj/core/change/brick_test.clj"
                                         "components/change/test/polylith/clj/core/change/core_test.clj"
                                         "components/deps/src/polylith/clj/core/deps/interface.clj"]
          :changed-or-affected-projects ["cli"
                                         "core"
                                         "dev"]
          :changed-projects             []
          :git-command                  "git diff --name-only"
          :project-to-bricks-to-test    {"cli"  []
                                         "core" ["change"
                                                 "deps"]
                                         "dev"  ["change"]}
          :project-to-indirect-changes  {"cli"  {:src  ["cli"
                                                        "command"
                                                        "validator"
                                                        "workspace"]
                                                 :test []}
                                         "core" {:src  ["cli"
                                                        "command"
                                                        "validator"
                                                        "workspace"]
                                                 :test []}
                                         "dev"  {:src  []
                                                 :test []}}
          :project-to-projects-to-test  {"cli"  []
                                         "core" []
                                         "dev"  []}}
         (core/changes workspace-with-active-dev {:files files} nil))))

(deftest changes--a-list-of-changed-files-and-projects-when-run-all--returns-changed-bricks-and-bricks-to-test2
  (is (= {:changed-bases                []
          :changed-components           ["change"
                                         "deps"]
          :changed-files                ["components/change/test/polylith/clj/core/change/brick_test.clj"
                                         "components/change/test/polylith/clj/core/change/core_test.clj"
                                         "components/deps/src/polylith/clj/core/deps/interface.clj"]
          :changed-or-affected-projects ["cli"
                                         "core"
                                         "dev"]
          :changed-projects             []
          :git-command                  "git diff --name-only"
          :project-to-bricks-to-test    {"cli"  []
                                         "core" ["change"
                                                 "common"
                                                 "deps"
                                                 "file"
                                                 "git"
                                                 "help"
                                                 "shell"]
                                         "dev"  []}
          :project-to-indirect-changes  {"cli"  {:src  ["cli"
                                                        "command"
                                                        "validator"
                                                        "workspace"]
                                                 :test []}
                                         "core" {:src  ["cli"
                                                        "command"
                                                        "validator"
                                                        "workspace"]
                                                 :test []}
                                         "dev"  {:src  []
                                                 :test []}}
          :project-to-projects-to-test  {"cli"  []
                                         "core" []
                                         "dev"  []}}
         (core/changes workspace-with-run-all-brick-tests-flags {:files files} nil))))
