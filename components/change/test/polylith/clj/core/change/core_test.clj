(ns polylith.clj.core.change.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.change.core :as core]))

(def files ["components/change/test/polylith/clj/core/change/brick_test.clj"
            "components/change/test/polylith/clj/core/change/core_test.clj"
            "components/deps/src/polylith/clj/core/deps/interfc.clj"])

(def environments [{:name "cli"
                    :dev? false
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
                    :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
                           "test-runner" {:direct ["common" "util"], :indirect []}
                           "command" {:direct ["common" "help" "test-runner" "util" "workspace"]
                                      :indirect ["deps" "file" "text-table" "validate"]}
                           "text-table" {:direct ["util"], :indirect []}
                           "util" {:direct [], :indirect []}
                           "validate" {:direct ["common" "deps" "util"], :indirect []}
                           "shell" {:direct [], :indirect []}
                           "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []}
                           "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                                  :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validate"]}
                           "git" {:direct ["shell"], :indirect []}
                           "deps" {:direct [], :indirect []}
                           "help" {:direct ["util"], :indirect []}
                           "file" {:direct [], :indirect []}
                           "common" {:direct ["util"], :indirect []}
                           "change" {:direct ["git" "util"], :indirect ["shell"]}}}
                   {:name "core"
                    :dev? false
                    :test-component-names []
                    :component-names ["change" "common" "deps" "file" "git" "help" "shell" "text-table" "util" "validate" "workspace"]
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
                                "components/validate/src"
                                "components/workspace/src"]
                    :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
                           "test-runner" {:direct ["common" "util"], :indirect []}
                           "command" {:direct ["common" "help" "util" "workspace"], :indirect ["deps" "file" "text-table" "validate"]}
                           "text-table" {:direct ["util"], :indirect []}
                           "util" {:direct [], :indirect []}
                           "validate" {:direct ["common" "deps" "util"], :indirect []}
                           "shell" {:direct [], :indirect []}
                           "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []}
                           "cli" {:direct ["change" "file" "workspace"]
                                  :indirect ["common" "deps" "git" "shell" "text-table" "util" "validate"]}
                           "git" {:direct ["shell"], :indirect []}
                           "deps" {:direct [], :indirect []}
                           "help" {:direct ["util"], :indirect []}
                           "file" {:direct [], :indirect []}
                           "common" {:direct ["util"], :indirect []}
                           "change" {:direct ["git" "util"], :indirect ["shell"]}}}
                   {:name "dev"
                    :dev? true
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
                                "components/validate/src"
                                "components/workspace/src"
                                "components/workspace-clj/src"]
                    :deps {"workspace-clj" {:direct ["common" "file" "util"], :indirect []}
                           "test-runner" {:direct ["common" "util"], :indirect []}
                           "command" {:direct ["common" "help" "test-runner" "util" "workspace"]
                                      :indirect ["deps" "file" "text-table" "validate"]}
                           "text-table" {:direct ["util"], :indirect []}
                           "util" {:direct [], :indirect []}
                           "validate" {:direct ["common" "deps" "util"], :indirect []}
                           "shell" {:direct [], :indirect []}
                           "workspace" {:direct ["common" "deps" "file" "text-table" "util" "validate"], :indirect []}
                           "cli" {:direct ["change" "command" "file" "workspace" "workspace-clj"]
                                  :indirect ["common" "deps" "git" "help" "shell" "test-runner" "text-table" "util" "validate"]}
                           "git" {:direct ["shell"], :indirect []}
                           "deps" {:direct [], :indirect []}
                           "help" {:direct ["util"], :indirect []}
                           "file" {:direct [], :indirect []}
                           "common" {:direct ["util"], :indirect []}
                           "change" {:direct ["git" "util"], :indirect ["shell"]}}}])

(def workspace {:environments environments})

(deftest changes--a-list-of-changed-files-and-environments-exclude-dev--returns-changed-bricks-and-bricks-to-test
  (is (= {:git-command "git diff --name-only",
          :changed-components ["change" "deps"]
          :changed-bases []
          :changed-environments []
          :environments-to-test []
          :env->indirect-changes {"cli" ["cli" "command" "validate" "workspace"]
                                  "core" ["cli" "command" "validate" "workspace"]
                                  "dev" ["cli" "command" "validate" "workspace"]}
          :env->bricks-to-test {"cli" []
                                "core" []
                                "dev" []}
          :changed-files ["components/change/test/polylith/clj/core/change/brick_test.clj"
                          "components/change/test/polylith/clj/core/change/core_test.clj"
                          "components/deps/src/polylith/clj/core/deps/interfc.clj"]}
         (core/changes workspace {:files files} false))))

(deftest changes--a-list-of-changed-files-and-environments-include-dev--returns-changed-bricks-and-bricks-to-test
  (is (= {:git-command "git diff --name-only",
          :changed-components ["change" "deps"]
          :changed-bases []
          :changed-environments []
          :environments-to-test []
          :env->indirect-changes {"cli" ["cli" "command" "validate" "workspace"]
                                  "core" ["cli" "command" "validate" "workspace"]
                                  "dev" ["cli" "command" "validate" "workspace"]}
          :env->bricks-to-test {"cli" []
                                "core" []
                                "dev" ["change" "cli" "command" "deps" "validate" "workspace"]}
          :changed-files ["components/change/test/polylith/clj/core/change/brick_test.clj"
                          "components/change/test/polylith/clj/core/change/core_test.clj"
                          "components/deps/src/polylith/clj/core/deps/interfc.clj"]}
         (core/changes workspace {:files files} true))))
