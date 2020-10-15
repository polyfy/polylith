(ns polylith.clj.core.change.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.core :as core]))

(def files ["components/change/test/polylith/clj/core/change/brick_test.clj"
            "components/change/test/polylith/clj/core/change/core_test.clj"
            "components/deps/src/polylith/clj/core/deps/interface.clj"])

(def projects [{:name                 "cli"
                :is-run-tests         true
                :is-dev               false
                :test-component-names []
                :component-names      ["change"
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
                                       "workspace-clj"]
                :base-names           ["cli"]
                :test-base-names      []
                :deps                 {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
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
                                       "change" {:direct ["git" "util"], :indirect ["shell"]}}}
               {:name "core"
                :is-run-tests true
                :is-dev false
                :test-component-names ["change" "common" "deps" "file" "git" "help" "shell"]
                :component-names ["change" "common" "deps" "file" "git" "help" "shell" "text-table" "util" "validator" "workspace"]
                :base-names []
                :test-base-names []
                :src-paths ["components/change/src"
                            "components/common/src"
                            "components/deps/src"
                            "components/file/src"
                            "components/git/src"
                            "components/help/src"
                            "components/shell/src"
                            "components/text-table/src"
                            "components/util/src"
                            "components/validator/src"
                            "components/workspace/src"]
                :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
                       "test-runner" {:direct ["common" "util"], :indirect []}
                       "command" {:direct ["common" "help" "util" "workspace"], :indirect ["deps" "file" "text-table" "validator"]}
                       "text-table" {:direct ["util"], :indirect []}
                       "util" {:direct [], :indirect []}
                       "validator" {:direct ["common" "deps" "util"], :indirect []}
                       "shell" {:direct [], :indirect []}
                       "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validator"], :indirect []}
                       "cli" {:direct ["change" "file" "workspace"]
                              :indirect ["common" "deps" "git" "shell" "text-table" "util" "validator"]}
                       "git" {:direct ["shell"], :indirect []}
                       "deps" {:direct [], :indirect []}
                       "help" {:direct ["util"], :indirect []}
                       "file" {:direct [], :indirect []}
                       "common" {:direct ["util"], :indirect []}
                       "change" {:direct ["git" "util"], :indirect ["shell"]}}}
               {:name "dev"
                :is-run-tests false
                :is-dev true
                :test-component-names ["change" "common" "shell"]
                :component-names ["change"
                                  "command"
                                  "shell"]
                :base-names ["cli"]
                :test-base-names ["cli"]
                :src-paths ["bases/cli/src"
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
                            "components/workspace-clj/src"]
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
                       "change" {:direct ["git" "util"], :indirect ["shell"]}}}])

(def workspace {:projects projects
                :user-input   {}
                :paths        {:missing []}})

(def workspace-with-active-dev (assoc-in workspace [:projects 2 :is-run-tests] true))

(def workspace-with-run-all-brick-tests-flags (assoc workspace :user-input {:is-run-all-brick-tests true
                                                                            :is-run-project-tests true}))

(deftest changes--a-list-of-changed-files-and-projects--returns-changed-bricks-and-bricks-to-test
  (is (= {:git-command "git diff --name-only"
          :changed-components ["change" "deps"]
          :changed-bases []
          :changed-projects []
          :changed-or-affected-projects ["cli" "core" "dev"]
          :project-to-indirect-changes {"cli" ["cli" "command" "validator" "workspace"]
                                        "core" ["cli" "command" "validator" "workspace"]
                                        "dev" ["cli" "command" "validator" "workspace"]}
          :project-to-bricks-to-test {"cli" []
                                      "core" ["change" "deps"]
                                      "dev" []}
          :project-to-projects-to-test {"cli"  []
                                        "core" []
                                        "dev"  []}
          :changed-files ["components/change/test/polylith/clj/core/change/brick_test.clj"
                          "components/change/test/polylith/clj/core/change/core_test.clj"
                          "components/deps/src/polylith/clj/core/deps/interface.clj"]}
         (core/changes workspace {:files files}))))

(deftest changes--a-list-of-changed-files-and-active-dev--returns-changed-bricks-and-bricks-to-test
  (is (= {:git-command "git diff --name-only"
          :changed-components ["change" "deps"]
          :changed-bases []
          :changed-projects []
          :changed-or-affected-projects ["cli" "core" "dev"]
          :project-to-indirect-changes {"cli" ["cli" "command" "validator" "workspace"]
                                        "core" ["cli" "command" "validator" "workspace"]
                                        "dev" ["cli" "command" "validator" "workspace"]}
          :project-to-bricks-to-test {"cli" []
                                      "core" ["change" "deps"]
                                      "dev" ["change" "cli"]}
          :project-to-projects-to-test {"cli"  []
                                        "core" []
                                        "dev"  []}
          :changed-files ["components/change/test/polylith/clj/core/change/brick_test.clj"
                          "components/change/test/polylith/clj/core/change/core_test.clj"
                          "components/deps/src/polylith/clj/core/deps/interface.clj"]}
         (core/changes workspace-with-active-dev {:files files}))))

(deftest changes--a-list-of-changed-files-and-projects--returns-changed-bricks-and-bricks-to-test2
  (is (= {:git-command "git diff --name-only"
          :changed-components ["change" "deps"]
          :changed-bases []
          :changed-projects []
          :changed-or-affected-projects ["cli" "core" "dev"]
          :project-to-indirect-changes {"cli" ["cli" "command" "validator" "workspace"]
                                        "core" ["cli" "command" "validator" "workspace"]
                                        "dev" ["cli" "command" "validator" "workspace"]}
          :project-to-bricks-to-test {"cli" []
                                      "core" ["change" "common" "deps" "file" "git" "help" "shell"]
                                      "dev" []}
          :project-to-projects-to-test {"cli"  []
                                        "core" []
                                        "dev"  []}
          :changed-files ["components/change/test/polylith/clj/core/change/brick_test.clj"
                          "components/change/test/polylith/clj/core/change/core_test.clj"
                          "components/deps/src/polylith/clj/core/deps/interface.clj"]}
         (core/changes workspace-with-run-all-brick-tests-flags {:files files}))))
