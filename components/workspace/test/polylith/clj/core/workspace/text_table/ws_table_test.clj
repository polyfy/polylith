(ns polylith.clj.core.workspace.text-table.ws-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.workspace.text-table.ws-table.core :as ws-table]))

(def workspace {:interfaces [{:name "workspace-clj",
                              :type "interface",
                              :definitions [{:name "workspace-from-disk", :type "function", :parameters [{:name "ws-dir"}]}
                                            {:name "workspace-from-disk",
                                             :type "function",
                                             :parameters [{:name "ws-dir"} {:name "config"}]}],
                              :implementing-components ["workspace-clj"]}
                             {:name "test-runner",
                              :type "interface",
                              :definitions [{:name "run", :type "function", :parameters [{:name "workspace"}]}],
                              :implementing-components ["test-runner"]}
                             {:name "command",
                              :type "interface",
                              :definitions [{:name "execute-command",
                                             :type "function",
                                             :parameters [{:name "current-dir"} {:name "workspace"} {:name "cmd"} {:name "user-input"}]}],
                              :implementing-components ["command"]}
                             {:name "text-table",
                              :type "interface",
                              :definitions [{:name "line", :type "function", :parameters [{:name "rows"}]}
                                            {:name "line", :type "function", :parameters [{:name "rows"} {:name "visables"}]}
                                            {:name "table",
                                             :type "function",
                                             :parameters [{:name "initial-spaces"}
                                                          {:name "alignments"}
                                                          {:name "colors"}
                                                          {:name "rows"}
                                                          {:name "color-mode"}]}
                                            {:name "table",
                                             :type "function",
                                             :parameters [{:name "initial-spaces"}
                                                          {:name "alignments"}
                                                          {:name "header-colors"}
                                                          {:name "header-orientations"}
                                                          {:name "colors"}
                                                          {:name "headers"}
                                                          {:name "rows"}
                                                          {:name "color-mode"}]}
                                            {:name "table",
                                             :type "function",
                                             :parameters [{:name "initial-spaces"}
                                                          {:name "alignments"}
                                                          {:name "header-colors"}
                                                          {:name "header-orientations"}
                                                          {:name "colors"}
                                                          {:name "headers"}
                                                          {:name "line-visables"}
                                                          {:name "rows"}
                                                          {:name "color-mode"}]}],
                              :implementing-components ["text-table"]}
                             {:name "util",
                              :type "interface",
                              :definitions [{:name "find-first",
                                             :type "function",
                                             :parameters [{:name "predicate"} {:name "sequence"}]}
                                            {:name "first-as-vector", :type "function", :parameters [{:name "vals"}]}
                                            {:name "ordered-map", :type "function", :parameters [{:name "&"} {:name "keyvals"}]}
                                            {:name "stringify-and-sort-map", :type "function", :parameters [{:name "m"}]}
                                            {:name "def-keys", :type "macro", :parameters [{:name "amap"} {:name "keys"}]}
                                            {:name "none", :type "data", :sub-ns "color"}
                                            {:name "base",
                                             :type "function",
                                             :parameters [{:name "base"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "blue",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "brick",
                                             :type "function",
                                             :parameters [{:name "type"} {:name "brick"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "clean-colors", :type "function", :parameters [{:name "message"}], :sub-ns "color"}
                                            {:name "colored-text",
                                             :type "function",
                                             :parameters [{:name "color"} {:name "color-mode"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "colored-text",
                                             :type "function",
                                             :parameters [{:name "color-light"}
                                                          {:name "color-dark"}
                                                          {:name "color-mode"}
                                                          {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "component",
                                             :type "function",
                                             :parameters [{:name "component"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "cyan",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "environment",
                                             :type "function",
                                             :parameters [{:name "env"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "error",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "green",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "grey",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "interface",
                                             :type "function",
                                             :parameters [{:name "ifc"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "namespc",
                                             :type "function",
                                             :parameters [{:name "namespace"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "namespc",
                                             :type "function",
                                             :parameters [{:name "interface"} {:name "namespace"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "ok",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "purple",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "red",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "warning",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "yellow",
                                             :type "function",
                                             :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "print-error-message",
                                             :type "function",
                                             :parameters [{:name "e"}],
                                             :sub-ns "exception"}
                                            {:name "print-exception", :type "function", :parameters [{:name "e"}], :sub-ns "exception"}
                                            {:name "print-stacktrace",
                                             :type "function",
                                             :parameters [{:name "e"}],
                                             :sub-ns "exception"}
                                            {:name "extract", :type "function", :parameters [{:name "args"}], :sub-ns "params"}
                                            {:name "key-name", :type "function", :parameters [{:name "arg"}], :sub-ns "params"}
                                            {:name "named?", :type "function", :parameters [{:name "arg"}], :sub-ns "params"}
                                            {:name "unnamed?", :type "function", :parameters [{:name "arg"}], :sub-ns "params"}
                                            {:name "count-things",
                                             :type "function",
                                             :parameters [{:name "thing"} {:name "cnt"}],
                                             :sub-ns "str"}
                                            {:name "line", :type "function", :parameters [{:name "length"}], :sub-ns "str"}
                                            {:name "sep-1000",
                                             :type "function",
                                             :parameters [{:name "number"} {:name "sep"}],
                                             :sub-ns "str"}
                                            {:name "skip-if-ends-with",
                                             :type "function",
                                             :parameters [{:name "string"} {:name "ends-with"}],
                                             :sub-ns "str"}
                                            {:name "skip-prefix",
                                             :type "function",
                                             :parameters [{:name "string"} {:name "prefix"}],
                                             :sub-ns "str"}
                                            {:name "skip-suffix",
                                             :type "function",
                                             :parameters [{:name "string"} {:name "suffix"}],
                                             :sub-ns "str"}
                                            {:name "skip-suffixes",
                                             :type "function",
                                             :parameters [{:name "string"} {:name "suffixes"}],
                                             :sub-ns "str"}
                                            {:name "skip-until",
                                             :type "function",
                                             :parameters [{:name "string"} {:name "separator"}],
                                             :sub-ns "str"}
                                            {:name "spaces", :type "function", :parameters [{:name "n#spaces"}], :sub-ns "str"}
                                            {:name "take-until",
                                             :type "function",
                                             :parameters [{:name "string"} {:name "separator"}],
                                             :sub-ns "str"}
                                            {:name "current-time", :type "function", :parameters [], :sub-ns "time"}
                                            {:name "print-execution-time",
                                             :type "function",
                                             :parameters [{:name "start-time"}],
                                             :sub-ns "time"}],
                              :implementing-components ["util"]}
                             {:name "validate",
                              :type "interface",
                              :definitions [{:name "messages",
                                             :type "function",
                                             :parameters [{:name "ws-dir"}
                                                          {:name "suffixed-top-ns"}
                                                          {:name "interface-names"}
                                                          {:name "interfaces"}
                                                          {:name "components"}
                                                          {:name "bases"}
                                                          {:name "environments"}
                                                          {:name "interface-ns"}
                                                          {:name "ns->lib"}
                                                          {:name "color-mode"}]}],
                              :implementing-components ["validate"]}
                             {:name "shell",
                              :type "interface",
                              :definitions [{:name "sh", :type "function", :parameters [{:name "&"} {:name "args"}]}],
                              :implementing-components ["shell"]}
                             {:name "workspace",
                              :type "interface",
                              :definitions [{:name "enrich-workspace",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "user-input"}]}
                                            {:name "enrich-workspace-str-keys",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "user-input"}]}
                                            {:name "print-info",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "show-loc?"}]}
                                            {:name "print-table-str-keys",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "show-loc?"}]}],
                              :implementing-components ["workspace"]}
                             {:name "user-config",
                              :type "interface",
                              :definitions [{:name "color-mode", :type "function", :parameters []}
                                            {:name "config-content", :type "function", :parameters []}
                                            {:name "home-dir", :type "function", :parameters []}
                                            {:name "thousand-separator", :type "function", :parameters []}],
                              :implementing-components ["user-config"]}
                             {:name "git",
                              :type "interface",
                              :definitions [{:name "add", :type "function", :parameters [{:name "ws-dir"} {:name "filename"}]}
                                            {:name "current-sha", :type "function", :parameters [{:name "ws-dir"}]}
                                            {:name "diff",
                                             :type "function",
                                             :parameters [{:name "ws-dir"} {:name "sha1"} {:name "sha2"}]}
                                            {:name "diff-command", :type "function", :parameters [{:name "sha1"} {:name "sha2"}]}
                                            {:name "init", :type "function", :parameters [{:name "ws-dir"}]}],
                              :implementing-components ["git"]}
                             {:name "deps",
                              :type "interface",
                              :definitions [{:name "interface-deps",
                                             :type "function",
                                             :parameters [{:name "suffixed-top-ns"} {:name "interface-names"} {:name "brick"}]}
                                            {:name "interface-ns-deps",
                                             :type "function",
                                             :parameters [{:name "top-ns"}
                                                          {:name "interface-name"}
                                                          {:name "interface-names"}
                                                          {:name "brick-namespaces"}]}
                                            {:name "print-brick-ifc-table",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "brick-name"} {:name "color-mode"}]}
                                            {:name "print-brick-table",
                                             :type "function",
                                             :parameters [{:name "workspace"}
                                                          {:name "environment-name"}
                                                          {:name "brick-name"}
                                                          {:name "color-mode"}]}
                                            {:name "print-workspace-brick-table",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "environment-name"} {:name "color-mode"}]}
                                            {:name "print-workspace-ifc-table",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "color-mode"}]}],
                              :implementing-components ["deps"]}
                             {:name "help",
                              :type "interface",
                              :definitions [{:name "print-help", :type "function", :parameters [{:name "cmd"} {:name "color-mode"}]}],
                              :implementing-components ["help"]}
                             {:name "create",
                              :type "interface",
                              :definitions [{:name "create-base",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "base-name"}]}
                                            {:name "create-component",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "component-name"} {:name "interface-name"}]}
                                            {:name "create-environment",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "env"}]}
                                            {:name "create-workspace",
                                             :type "function",
                                             :parameters [{:name "root-dir"} {:name "ws-name"} {:name "top-ns"}]}
                                            {:name "print-alias-message",
                                             :type "function",
                                             :parameters [{:name "env"} {:name "color-mode"}]}],
                              :implementing-components ["create"]}
                             {:name "file",
                              :type "interface",
                              :definitions [{:name "absolute-path", :type "function", :parameters [{:name "path"}]}
                                            {:name "copy-resource-file!",
                                             :type "function",
                                             :parameters [{:name "source"} {:name "target-path"}]}
                                            {:name "create-dir", :type "function", :parameters [{:name "path", :type "^String"}]}
                                            {:name "create-file", :type "function", :parameters [{:name "path"} {:name "rows"}]}
                                            {:name "create-missing-dirs", :type "function", :parameters [{:name "filename"}]}
                                            {:name "create-temp-dir", :type "function", :parameters [{:name "dir"}]}
                                            {:name "current-dir", :type "function", :parameters []}
                                            {:name "delete-dir", :type "function", :parameters [{:name "path"}]}
                                            {:name "delete-file", :type "function", :parameters [{:name "path"}]}
                                            {:name "delete-folder", :type "function", :parameters [{:name "file"}]}
                                            {:name "directory-paths", :type "function", :parameters [{:name "dir"}]}
                                            {:name "directory?", :type "function", :parameters [{:name "file", :type "^File"}]}
                                            {:name "exists", :type "function", :parameters [{:name "path"}]}
                                            {:name "file-name", :type "function", :parameters [{:name "file", :type "^File"}]}
                                            {:name "files-recursively", :type "function", :parameters [{:name "dir-path"}]}
                                            {:name "lines-of-code", :type "function", :parameters [{:name "file-path"}]}
                                            {:name "paths-recursively", :type "function", :parameters [{:name "dir"}]}
                                            {:name "read-file", :type "function", :parameters [{:name "path"}]}
                                            {:name "relative-paths", :type "function", :parameters [{:name "path"}]}],
                              :implementing-components ["file"]}
                             {:name "entity",
                              :type "interface",
                              :definitions [{:name "all-src-deps", :type "function", :parameters [{:name "dep-entries"}]}
                                            {:name "all-test-deps", :type "function", :parameters [{:name "dep-entries"}]}
                                            {:name "brick-status-flags",
                                             :type "function",
                                             :parameters [{:name "path-entries"} {:name "brick-name"}]}
                                            {:name "deps-entries",
                                             :type "function",
                                             :parameters [{:name "dev?"} {:name "src-deps"} {:name "test-deps"} {:name "settings"}]}
                                            {:name "env-status-flags",
                                             :type "function",
                                             :parameters [{:name "path-entries"} {:name "env-name"}]}
                                            {:name "path-entries",
                                             :type "function",
                                             :parameters [{:name "ws-dir"}
                                                          {:name "dev?"}
                                                          {:name "src-paths"}
                                                          {:name "test-paths"}
                                                          {:name "settings"}]}
                                            {:name "src-base-names", :type "function", :parameters [{:name "path-entries"}]}
                                            {:name "src-brick-names", :type "function", :parameters [{:name "path-entries"}]}
                                            {:name "src-component-names", :type "function", :parameters [{:name "path-entries"}]}
                                            {:name "src-paths", :type "function", :parameters [{:name "path-entries"}]}
                                            {:name "test-base-names", :type "function", :parameters [{:name "path-entries"}]}
                                            {:name "test-component-names", :type "function", :parameters [{:name "path-entries"}]}
                                            {:name "test-paths", :type "function", :parameters [{:name "path-entries"}]}],
                              :implementing-components ["entity"]}
                             {:name "test-helper",
                              :type "interface",
                              :definitions [{:name "content", :type "function", :parameters [{:name "dir"} {:name "filename"}]}
                                            {:name "execute-command",
                                             :type "function",
                                             :parameters [{:name "current-dir"} {:name "cmd"} {:name "&"} {:name "[arg1 arg2 arg3]"}]}
                                            {:name "paths", :type "function", :parameters [{:name "dir"}]}
                                            {:name "root-dir", :type "function", :parameters []}
                                            {:name "test-setup-and-tear-down", :type "function", :parameters [{:name "function"}]}
                                            {:name "user-home", :type "function", :parameters []}],
                              :implementing-components ["test-helper"]}
                             {:name "common",
                              :type "interface",
                              :definitions [{:name "create-class-loader",
                                             :type "function",
                                             :parameters [{:name "paths"} {:name "color-mode"}]}
                                            {:name "eval-in", :type "function", :parameters [{:name "class-loader"} {:name "form"}]}
                                            {:name "filter-clojure-paths", :type "function", :parameters [{:name "paths"}]}
                                            {:name "find-base", :type "function", :parameters [{:name "base-name"} {:name "bases"}]}
                                            {:name "find-brick", :type "function", :parameters [{:name "name"} {:name "workspace"}]}
                                            {:name "find-component",
                                             :type "function",
                                             :parameters [{:name "name"} {:name "components"}]}
                                            {:name "find-environment",
                                             :type "function",
                                             :parameters [{:name "environment-name"} {:name "environments"}]}
                                            {:name "messages-without-colors", :type "function", :parameters [{:name "workspace"}]}
                                            {:name "ns-to-path", :type "function", :parameters [{:name "namespace"}]}
                                            {:name "path-to-ns", :type "function", :parameters [{:name "namespace"}]}
                                            {:name "pretty-messages", :type "function", :parameters [{:name "workspace"}]}
                                            {:name "pretty-messages",
                                             :type "function",
                                             :parameters [{:name "messages"} {:name "color-mode"}]}
                                            {:name "suffix-ns-with-dot", :type "function", :parameters [{:name "namespace"}]}
                                            {:name "user-input", :type "function", :parameters [{:name "args"}]}
                                            {:name "validate-args",
                                             :type "function",
                                             :parameters [{:name "unnamed-args"} {:name "example"}]}
                                            {:name "base-paths", :type "function", :parameters [{:name "paths"}], :sub-ns "paths"}
                                            {:name "bases-from-paths",
                                             :type "function",
                                             :parameters [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "bricks-from-paths",
                                             :type "function",
                                             :parameters [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "component-paths", :type "function", :parameters [{:name "paths"}], :sub-ns "paths"}
                                            {:name "components-from-paths",
                                             :type "function",
                                             :parameters [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "environments-from-paths",
                                             :type "function",
                                             :parameters [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "src-path?", :type "function", :parameters [{:name "path"}], :sub-ns "paths"}
                                            {:name "test-path?", :type "function", :parameters [{:name "path"}], :sub-ns "paths"}],
                              :implementing-components ["common"]}
                             {:name "change",
                              :type "interface",
                              :definitions [{:name "with-changes",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "user-input"}]}
                                            {:name "with-changes",
                                             :type "function",
                                             :parameters [{:name "workspace"} {:name "changed-files"} {:name "user-input"}]}],
                              :implementing-components ["change"]}],
                :ws-dir ".",
                :name "polylith",
                :settings {:active-dev-profiles #{"default"},
                           :top-namespace "polylith.clj.core",
                           :profile->settings {},
                           :ns->lib {"clojure" "org.clojure/clojure",
                                     "clojure.core.matrix" "net.mikera/core.matrix",
                                     "clojure.tools.deps" "org.clojure/tools.deps.alpha"},
                           :env->alias {"cli" "cli", "core" "core"},
                           :interface-ns "interfc",
                           :vcs "git",
                           :thousand-sep ",",
                           :color-mode "none"},
                :ws-reader {:name "polylith-clj",
                            :project-url "https://github.com/tengstrand/polylith/tree/core",
                            :reader-version "1.0",
                            :ws-contract-version 1,
                            :language "Clojure",
                            :type-position "postfix",
                            :slash "/",
                            :file-extensions [".clj" "cljc"]},
                :environments [{:name "cli",
                                :alias "cli",
                                :type "environment",
                                :active? true,
                                :dev? false,
                                :env-dir "./environments/cli",
                                :config-file "./environments/cli/deps.edn",
                                :lines-of-code-src 0,
                                :lines-of-code-test 5,
                                :total-lines-of-code-src 4558,
                                :total-lines-of-code-test 3520,
                                :test-component-names ["change"
                                                       "command"
                                                       "common"
                                                       "create"
                                                       "deps"
                                                       "entity"
                                                       "file"
                                                       "git"
                                                       "text-table"
                                                       "util"
                                                       "validate"
                                                       "workspace"
                                                       "workspace-clj"],
                                :component-names ["change"
                                                  "command"
                                                  "common"
                                                  "create"
                                                  "deps"
                                                  "entity"
                                                  "file"
                                                  "git"
                                                  "help"
                                                  "shell"
                                                  "test-helper"
                                                  "test-runner"
                                                  "text-table"
                                                  "user-config"
                                                  "util"
                                                  "validate"
                                                  "workspace"
                                                  "workspace-clj"],
                                :base-names ["cli"],
                                :test-base-names [],
                                :has-src-dir? false,
                                :has-test-dir? true,
                                :namespaces-src [],
                                :namespaces-test [{:name "polylith.clj.core.env.cli",
                                                   :namespace "polylith.clj.core.env.cli",
                                                   :file-path "./environments/cli/test/polylith/clj/core/env/cli.clj",
                                                   :imports []}],
                                :src-paths ["bases/cli/src"
                                            "components/change/src"
                                            "components/command/src"
                                            "components/common/src"
                                            "components/create/resources"
                                            "components/create/src"
                                            "components/deps/src"
                                            "components/entity/src"
                                            "components/file/src"
                                            "components/git/src"
                                            "components/help/src"
                                            "components/shell/src"
                                            "components/test-helper/src"
                                            "components/test-runner/src"
                                            "components/text-table/src"
                                            "components/user-config/src"
                                            "components/util/src"
                                            "components/validate/src"
                                            "components/workspace-clj/src"
                                            "components/workspace/src"],
                                :test-paths ["bases/cli/test"
                                             "components/change/test"
                                             "components/command/test"
                                             "components/common/test"
                                             "components/create/test"
                                             "components/deps/test"
                                             "components/entity/test"
                                             "components/file/test"
                                             "components/git/test"
                                             "components/help/test"
                                             "components/shell/test"
                                             "components/test-helper/test"
                                             "components/test-runner/test"
                                             "components/text-table/test"
                                             "components/user-config/test"
                                             "components/util/test"
                                             "components/validate/test"
                                             "components/workspace-clj/test"
                                             "components/workspace/test"
                                             "environments/cli/test"],
                                :lib-imports ["clojure.core.matrix"
                                              "clojure.java.io"
                                              "clojure.java.shell"
                                              "clojure.pprint"
                                              "clojure.set"
                                              "clojure.stacktrace"
                                              "clojure.string"
                                              "clojure.tools.deps.alpha"
                                              "clojure.tools.deps.alpha.util.maven"
                                              "clojure.walk"],
                                :lib-imports-test ["clojure.string" "clojure.tools.deps.alpha.util.maven"],
                                :lib-deps {"net.mikera/core.matrix" #:mvn{:version "0.62.0"},
                                           "org.apache.logging.log4j/log4j-core" #:mvn{:version "2.13.3"},
                                           "org.apache.logging.log4j/log4j-slf4j-impl" #:mvn{:version "2.13.3"},
                                           "org.clojure/clojure" #:mvn{:version "1.10.1"},
                                           "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}},
                                :deps {"workspace-clj" {:direct ["common" "file" "user-config" "util"], :indirect []},
                                       "test-runner" {:direct ["common" "util"], :indirect []},
                                       "command" {:direct ["common"
                                                           "create"
                                                           "deps"
                                                           "help"
                                                           "test-runner"
                                                           "user-config"
                                                           "util"
                                                           "workspace"],
                                                  :indirect ["entity" "file" "git" "shell" "text-table" "validate"]},
                                       "text-table" {:direct ["util"], :indirect []},
                                       "util" {:direct [], :indirect []},
                                       "validate" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                       "shell" {:direct [], :indirect []},
                                       "workspace" {:direct ["common"
                                                             "deps"
                                                             "entity"
                                                             "file"
                                                             "text-table"
                                                             "util"
                                                             "validate"],
                                                    :indirect []},
                                       "cli" {:direct ["change" "command" "common" "file" "util" "workspace" "workspace-clj"],
                                              :indirect ["create"
                                                         "deps"
                                                         "entity"
                                                         "git"
                                                         "help"
                                                         "shell"
                                                         "test-runner"
                                                         "text-table"
                                                         "user-config"
                                                         "validate"]},
                                       "user-config" {:direct ["util"], :indirect []},
                                       "git" {:direct ["shell"], :indirect []},
                                       "deps" {:direct ["common" "text-table" "util"], :indirect []},
                                       "help" {:direct ["util"], :indirect []},
                                       "create" {:direct ["common" "file" "git" "user-config" "util"], :indirect ["shell"]},
                                       "file" {:direct ["util"], :indirect []},
                                       "entity" {:direct ["file" "util"], :indirect []},
                                       "test-helper" {:direct ["change"
                                                               "command"
                                                               "common"
                                                               "file"
                                                               "git"
                                                               "user-config"
                                                               "workspace"
                                                               "workspace-clj"],
                                                      :indirect ["create"
                                                                 "deps"
                                                                 "entity"
                                                                 "help"
                                                                 "shell"
                                                                 "test-runner"
                                                                 "text-table"
                                                                 "util"
                                                                 "validate"]},
                                       "common" {:direct ["util"], :indirect []},
                                       "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},
                                :test-lib-deps {},
                                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                              "clojars" {:url "https://repo.clojars.org/"}}}
                               {:name "core",
                                :alias "core",
                                :type "environment",
                                :active? true,
                                :dev? false,
                                :env-dir "./environments/core",
                                :config-file "./environments/core/deps.edn",
                                :lines-of-code-src 0,
                                :lines-of-code-test 6,
                                :total-lines-of-code-src 3699,
                                :total-lines-of-code-test 3088,
                                :test-component-names [],
                                :component-names ["change"
                                                  "common"
                                                  "deps"
                                                  "entity"
                                                  "file"
                                                  "git"
                                                  "help"
                                                  "shell"
                                                  "text-table"
                                                  "user-config"
                                                  "util"
                                                  "validate"
                                                  "workspace"],
                                :base-names [],
                                :test-base-names [],
                                :has-src-dir? false,
                                :has-test-dir? false,
                                :namespaces-src [],
                                :namespaces-test [{:name "polylith.clj.core.dev-test",
                                                   :namespace "polylith.clj.core.dev-test",
                                                   :file-path "./environments/core/test/polylith/clj/core/dev_test.clj",
                                                   :imports []}],
                                :src-paths ["components/change/src"
                                            "components/common/src"
                                            "components/deps/src"
                                            "components/entity/src"
                                            "components/file/src"
                                            "components/git/src"
                                            "components/help/src"
                                            "components/shell/src"
                                            "components/text-table/src"
                                            "components/user-config/src"
                                            "components/util/src"
                                            "components/validate/src"
                                            "components/workspace/src"],
                                :test-paths [],
                                :lib-imports ["clojure.core.matrix"
                                              "clojure.java.io"
                                              "clojure.java.shell"
                                              "clojure.set"
                                              "clojure.stacktrace"
                                              "clojure.string"
                                              "clojure.walk"],
                                :lib-imports-test ["clojure.string"],
                                :lib-deps {"net.mikera/core.matrix" #:mvn{:version "0.62.0"},
                                           "org.clojure/clojure" #:mvn{:version "1.10.1"},
                                           "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}},
                                :deps {"workspace-clj" {:direct ["common" "file" "user-config" "util"], :indirect []},
                                       "test-runner" {:direct ["common" "util"], :indirect []},
                                       "command" {:direct ["common" "deps" "help" "user-config" "util" "workspace"],
                                                  :indirect ["entity" "file" "text-table" "text-table2" "validate"]},
                                       "text-table" {:direct ["util"], :indirect []},
                                       "util" {:direct [], :indirect []},
                                       "validate" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                       "text-table2" {:direct ["util"], :indirect []},
                                       "shell" {:direct [], :indirect []},
                                       "workspace" {:direct ["common"
                                                             "deps"
                                                             "entity"
                                                             "file"
                                                             "text-table"
                                                             "util"
                                                             "validate"],
                                                    :indirect []},
                                       "cli" {:direct ["change" "common" "file" "util" "workspace"],
                                              :indirect ["deps" "entity" "git" "shell" "text-table" "text-table2" "validate"]},
                                       "user-config" {:direct ["util"], :indirect []},
                                       "git" {:direct ["shell"], :indirect []},
                                       "deps" {:direct ["common" "text-table" "util"], :indirect []},
                                       "help" {:direct ["util"], :indirect []},
                                       "create" {:direct ["common" "file" "git" "user-config" "util"], :indirect ["shell"]},
                                       "file" {:direct ["util"], :indirect []},
                                       "entity" {:direct ["file" "util"], :indirect []},
                                       "test-helper" {:direct ["change" "common" "file" "git" "user-config" "workspace"],
                                                      :indirect ["deps" "entity" "shell" "text-table" "text-table2" "util" "validate"]},
                                       "common" {:direct ["util"], :indirect []},
                                       "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},
                                :test-lib-deps {},
                                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                              "clojars" {:url "https://repo.clojars.org/"}}}
                               {:name "development",
                                :alias "dev",
                                :type "environment",
                                :active? false,
                                :dev? true,
                                :env-dir "./development",
                                :config-file "./deps.edn",
                                :lines-of-code-src 100,
                                :lines-of-code-test 0,
                                :total-lines-of-code-src 4558,
                                :total-lines-of-code-test 3520,
                                :test-component-names ["change"
                                                       "command"
                                                       "common"
                                                       "create"
                                                       "deps"
                                                       "entity"
                                                       "file"
                                                       "git"
                                                       "text-table"
                                                       "util"
                                                       "validate"
                                                       "workspace"
                                                       "workspace-clj"],
                                :component-names ["change"
                                                  "command"
                                                  "common"
                                                  "create"
                                                  "deps"
                                                  "entity"
                                                  "file"
                                                  "git"
                                                  "help"
                                                  "shell"
                                                  "test-helper"
                                                  "test-runner"
                                                  "text-table"
                                                  "user-config"
                                                  "util"
                                                  "validate"
                                                  "workspace"
                                                  "workspace-clj"],
                                :base-names ["cli"],
                                :test-base-names [],
                                :has-src-dir? false,
                                :has-test-dir? false,
                                :namespaces-src [{:name "dev.jocke",
                                                  :namespace "dev.jocke",
                                                  :file-path "./development/src/dev/jocke.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.change.interfc"
                                                            "polylith.clj.core.common.interfc"
                                                            "polylith.clj.core.entity.interfc"
                                                            "polylith.clj.core.file.interfc"
                                                            "polylith.clj.core.help.interfc"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.workspace-clj.interfc"
                                                            "polylith.clj.core.workspace.interfc"]}],
                                :namespaces-test [],
                                :src-paths ["bases/cli/src"
                                            "components/change/src"
                                            "components/command/src"
                                            "components/common/src"
                                            "components/create/resources"
                                            "components/create/src"
                                            "components/deps/src"
                                            "components/entity/src"
                                            "components/file/src"
                                            "components/git/src"
                                            "components/help/src"
                                            "components/shell/src"
                                            "components/test-helper/src"
                                            "components/test-runner/src"
                                            "components/text-table/src"
                                            "components/user-config/src"
                                            "components/util/src"
                                            "components/validate/src"
                                            "components/workspace-clj/src"
                                            "components/workspace/src"
                                            "development/src"],
                                :test-paths ["bases/cli/test"
                                             "components/change/test"
                                             "components/command/test"
                                             "components/common/test"
                                             "components/create/test"
                                             "components/deps/test"
                                             "components/entity/test"
                                             "components/file/test"
                                             "components/git/test"
                                             "components/help/test"
                                             "components/shell/test"
                                             "components/test-helper/test"
                                             "components/test-runner/test"
                                             "components/text-table/test"
                                             "components/user-config/test"
                                             "components/util/test"
                                             "components/validate/test"
                                             "components/workspace-clj/test"
                                             "components/workspace/test"
                                             "environments/cli/test"],
                                :lib-imports ["clojure.core.matrix"
                                              "clojure.java.io"
                                              "clojure.java.shell"
                                              "clojure.pprint"
                                              "clojure.set"
                                              "clojure.stacktrace"
                                              "clojure.string"
                                              "clojure.tools.deps.alpha"
                                              "clojure.tools.deps.alpha.util.maven"
                                              "clojure.walk"],
                                :lib-imports-test ["clojure.string" "clojure.tools.deps.alpha.util.maven"],
                                :lib-deps {"net.mikera/core.matrix" #:mvn{:version "0.62.0"},
                                           "org.apache.logging.log4j/log4j-core" #:mvn{:version "2.13.3"},
                                           "org.apache.logging.log4j/log4j-slf4j-impl" #:mvn{:version "2.13.3"},
                                           "org.clojure/clojure" #:mvn{:version "1.10.1"},
                                           "org.clojure/tools.deps.alpha" #:mvn{:version "0.8.695"}},
                                :deps {"workspace-clj" {:direct ["common" "file" "user-config" "util"], :indirect []},
                                       "test-runner" {:direct ["common" "util"], :indirect []},
                                       "command" {:direct ["common"
                                                           "create"
                                                           "deps"
                                                           "help"
                                                           "test-runner"
                                                           "user-config"
                                                           "util"
                                                           "workspace"],
                                                  :indirect ["entity" "file" "git" "shell" "text-table" "text-table2" "validate"]},
                                       "text-table" {:direct ["util"], :indirect []},
                                       "util" {:direct [], :indirect []},
                                       "validate" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                       "text-table2" {:direct ["util"], :indirect []},
                                       "shell" {:direct [], :indirect []},
                                       "workspace" {:direct ["common"
                                                             "deps"
                                                             "entity"
                                                             "file"
                                                             "text-table"
                                                             "util"
                                                             "validate"],
                                                    :indirect []},
                                       "cli" {:direct ["change" "command" "common" "file" "util" "workspace" "workspace-clj"],
                                              :indirect ["create"
                                                         "deps"
                                                         "entity"
                                                         "git"
                                                         "help"
                                                         "shell"
                                                         "test-runner"
                                                         "text-table"
                                                         "user-config"
                                                         "validate"]},
                                       "user-config" {:direct ["util"], :indirect []},
                                       "git" {:direct ["shell"], :indirect []},
                                       "deps" {:direct ["common" "text-table" "util"], :indirect []},
                                       "help" {:direct ["util"], :indirect []},
                                       "create" {:direct ["common" "file" "git" "user-config" "util"], :indirect ["shell"]},
                                       "file" {:direct ["util"], :indirect []},
                                       "entity" {:direct ["file" "util"], :indirect []},
                                       "test-helper" {:direct ["change"
                                                               "command"
                                                               "common"
                                                               "file"
                                                               "git"
                                                               "user-config"
                                                               "workspace"
                                                               "workspace-clj"],
                                                      :indirect ["create"
                                                                 "deps"
                                                                 "entity"
                                                                 "help"
                                                                 "shell"
                                                                 "test-runner"
                                                                 "text-table"
                                                                 "util"
                                                                 "validate"]},
                                       "common" {:direct ["util"], :indirect []},
                                       "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},
                                :test-lib-deps {},
                                :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                              "clojars" {:url "https://repo.clojars.org/"}}}],
                :messages [],
                :components [{:name "change",
                              :type "component",
                              :lines-of-code-src 134,
                              :lines-of-code-test 343,
                              :interface {:name "change",
                                          :definitions [{:name "with-changes",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "user-input"}]}
                                                        {:name "with-changes",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "changed-files"} {:name "user-input"}]}]},
                              :namespaces-src [{:name "to-test",
                                                :namespace "polylith.clj.core.change.to-test",
                                                :file-path "./components/change/src/polylith/clj/core/change/to_test.clj",
                                                :imports ["clojure.set" "polylith.clj.core.change.environment"]}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.change.interfc",
                                                :file-path "./components/change/src/polylith/clj/core/change/interfc.clj",
                                                :imports ["polylith.clj.core.change.core"]}
                                               {:name "environment",
                                                :namespace "polylith.clj.core.change.environment",
                                                :file-path "./components/change/src/polylith/clj/core/change/environment.clj",
                                                :imports ["clojure.set"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.change.core",
                                                :file-path "./components/change/src/polylith/clj/core/change/core.clj",
                                                :imports ["polylith.clj.core.change.entity"
                                                          "polylith.clj.core.change.indirect"
                                                          "polylith.clj.core.change.to-test"
                                                          "polylith.clj.core.git.interfc"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc"]}
                                               {:name "entity",
                                                :namespace "polylith.clj.core.change.entity",
                                                :file-path "./components/change/src/polylith/clj/core/change/entity.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.common.interfc.paths"
                                                          "polylith.clj.core.git.interfc"]}
                                               {:name "indirect",
                                                :namespace "polylith.clj.core.change.indirect",
                                                :file-path "./components/change/src/polylith/clj/core/change/indirect.clj",
                                                :imports ["clojure.set"]}],
                              :namespaces-test [{:name "core-test",
                                                 :namespace "polylith.clj.core.change.core-test",
                                                 :file-path "./components/change/test/polylith/clj/core/change/core_test.clj",
                                                 :imports ["polylith.clj.core.change.core"]}
                                                {:name "indirect-test",
                                                 :namespace "polylith.clj.core.change.indirect-test",
                                                 :file-path "./components/change/test/polylith/clj/core/change/indirect_test.clj",
                                                 :imports ["polylith.clj.core.change.indirect"]}
                                                {:name "brick-test",
                                                 :namespace "polylith.clj.core.change.brick-test",
                                                 :file-path "./components/change/test/polylith/clj/core/change/brick_test.clj",
                                                 :imports ["polylith.clj.core.change.entity"]}
                                                {:name "to-test-test",
                                                 :namespace "polylith.clj.core.change.to-test-test",
                                                 :file-path "./components/change/test/polylith/clj/core/change/to_test_test.clj",
                                                 :imports ["polylith.clj.core.change.to-test"]}
                                                {:name "environment-test",
                                                 :namespace "polylith.clj.core.change.environment-test",
                                                 :file-path "./components/change/test/polylith/clj/core/change/environment_test.clj",
                                                 :imports ["polylith.clj.core.change.environment"]}],
                              :lib-imports-src ["clojure.set" "clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["common" "git" "util"],
                              :lib-deps ["clojure"]}
                             {:name "command",
                              :type "component",
                              :lines-of-code-src 151,
                              :lines-of-code-test 0,
                              :interface {:name "command",
                                          :definitions [{:name "execute-command",
                                                         :type "function",
                                                         :parameters [{:name "current-dir"}
                                                                      {:name "workspace"}
                                                                      {:name "cmd"}
                                                                      {:name "user-input"}]}]},
                              :namespaces-src [{:name "create",
                                                :namespace "polylith.clj.core.command.create",
                                                :file-path "./components/command/src/polylith/clj/core/command/create.clj",
                                                :imports ["polylith.clj.core.command.message" "polylith.clj.core.create.interfc"]}
                                               {:name "deps",
                                                :namespace "polylith.clj.core.command.deps",
                                                :file-path "./components/command/src/polylith/clj/core/command/deps.clj",
                                                :imports ["polylith.clj.core.common.interfc" "polylith.clj.core.deps.interfc"]}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.command.interfc",
                                                :file-path "./components/command/src/polylith/clj/core/command/interfc.clj",
                                                :imports ["polylith.clj.core.command.core"]}
                                               {:name "exit-code",
                                                :namespace "polylith.clj.core.command.exit-code",
                                                :file-path "./components/command/src/polylith/clj/core/command/exit_code.clj",
                                                :imports []}
                                               {:name "info",
                                                :namespace "polylith.clj.core.command.info",
                                                :file-path "./components/command/src/polylith/clj/core/command/info.clj",
                                                :imports ["polylith.clj.core.common.interfc" "polylith.clj.core.workspace.interfc"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.command.core",
                                                :file-path "./components/command/src/polylith/clj/core/command/core.clj",
                                                :imports ["clojure.pprint"
                                                          "polylith.clj.core.command.create"
                                                          "polylith.clj.core.command.deps"
                                                          "polylith.clj.core.command.exit-code"
                                                          "polylith.clj.core.command.info"
                                                          "polylith.clj.core.command.message"
                                                          "polylith.clj.core.command.test"
                                                          "polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.help.interfc"
                                                          "polylith.clj.core.user-config.interfc"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.util.interfc.params"]}
                                               {:name "message",
                                                :namespace "polylith.clj.core.command.message",
                                                :file-path "./components/command/src/polylith/clj/core/command/message.clj",
                                                :imports []}
                                               {:name "test",
                                                :namespace "polylith.clj.core.command.test",
                                                :file-path "./components/command/src/polylith/clj/core/command/test.clj",
                                                :imports ["polylith.clj.core.common.interfc" "polylith.clj.core.test-runner.interfc"]}],
                              :namespaces-test [],
                              :lib-imports-src ["clojure.pprint"],
                              :lib-imports-test [],
                              :interface-deps ["common" "create" "deps" "help" "test-runner" "user-config" "util" "workspace"],
                              :lib-deps ["clojure"]}
                             {:name "common",
                              :type "component",
                              :lines-of-code-src 336,
                              :lines-of-code-test 53,
                              :interface {:name "common",
                                          :definitions [{:name "create-class-loader",
                                                         :type "function",
                                                         :parameters [{:name "paths"} {:name "color-mode"}]}
                                                        {:name "eval-in",
                                                         :type "function",
                                                         :parameters [{:name "class-loader"} {:name "form"}]}
                                                        {:name "filter-clojure-paths", :type "function", :parameters [{:name "paths"}]}
                                                        {:name "find-base",
                                                         :type "function",
                                                         :parameters [{:name "base-name"} {:name "bases"}]}
                                                        {:name "find-brick",
                                                         :type "function",
                                                         :parameters [{:name "name"} {:name "workspace"}]}
                                                        {:name "find-component",
                                                         :type "function",
                                                         :parameters [{:name "name"} {:name "components"}]}
                                                        {:name "find-environment",
                                                         :type "function",
                                                         :parameters [{:name "environment-name"} {:name "environments"}]}
                                                        {:name "messages-without-colors",
                                                         :type "function",
                                                         :parameters [{:name "workspace"}]}
                                                        {:name "ns-to-path", :type "function", :parameters [{:name "namespace"}]}
                                                        {:name "path-to-ns", :type "function", :parameters [{:name "namespace"}]}
                                                        {:name "pretty-messages", :type "function", :parameters [{:name "workspace"}]}
                                                        {:name "pretty-messages",
                                                         :type "function",
                                                         :parameters [{:name "messages"} {:name "color-mode"}]}
                                                        {:name "suffix-ns-with-dot",
                                                         :type "function",
                                                         :parameters [{:name "namespace"}]}
                                                        {:name "user-input", :type "function", :parameters [{:name "args"}]}
                                                        {:name "validate-args",
                                                         :type "function",
                                                         :parameters [{:name "unnamed-args"} {:name "example"}]}
                                                        {:name "base-paths",
                                                         :type "function",
                                                         :parameters [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "bases-from-paths",
                                                         :type "function",
                                                         :parameters [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "bricks-from-paths",
                                                         :type "function",
                                                         :parameters [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "component-paths",
                                                         :type "function",
                                                         :parameters [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "components-from-paths",
                                                         :type "function",
                                                         :parameters [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "environments-from-paths",
                                                         :type "function",
                                                         :parameters [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "src-path?",
                                                         :type "function",
                                                         :parameters [{:name "path"}],
                                                         :sub-ns "paths"}
                                                        {:name "test-path?",
                                                         :type "function",
                                                         :parameters [{:name "path"}],
                                                         :sub-ns "paths"}]},
                              :namespaces-src [{:name "user-input",
                                                :namespace "polylith.clj.core.common.user-input",
                                                :file-path "./components/common/src/polylith/clj/core/common/user_input.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.interfc.params"]}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.common.interfc",
                                                :file-path "./components/common/src/polylith/clj/core/common/interfc.clj",
                                                :imports ["polylith.clj.core.common.class-loader"
                                                          "polylith.clj.core.common.core"
                                                          "polylith.clj.core.common.message"
                                                          "polylith.clj.core.common.user-input"
                                                          "polylith.clj.core.common.validate-args"]}
                                               {:name "paths",
                                                :namespace "polylith.clj.core.common.paths",
                                                :file-path "./components/common/src/polylith/clj/core/common/paths.clj",
                                                :imports ["clojure.string"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.common.core",
                                                :file-path "./components/common/src/polylith/clj/core/common/core.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.interfc"]}
                                               {:name "message",
                                                :namespace "polylith.clj.core.common.message",
                                                :file-path "./components/common/src/polylith/clj/core/common/message.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.interfc.color"]}
                                               {:name "class-loader",
                                                :namespace "polylith.clj.core.common.class-loader",
                                                :file-path "./components/common/src/polylith/clj/core/common/class_loader.clj",
                                                :imports ["clojure.java.io" "clojure.string" "polylith.clj.core.util.interfc.color"]}
                                               {:name "interfc.paths",
                                                :namespace "polylith.clj.core.common.interfc.paths",
                                                :file-path "./components/common/src/polylith/clj/core/common/interfc/paths.clj",
                                                :imports ["polylith.clj.core.common.paths"]}
                                               {:name "validate-args",
                                                :namespace "polylith.clj.core.common.validate-args",
                                                :file-path "./components/common/src/polylith/clj/core/common/validate_args.clj",
                                                :imports ["clojure.string"]}],
                              :namespaces-test [{:name "user-input-test",
                                                 :namespace "polylith.clj.core.common.user-input-test",
                                                 :file-path "./components/common/test/polylith/clj/core/common/user_input_test.clj",
                                                 :imports ["polylith.clj.core.common.user-input"]}],
                              :lib-imports-src ["clojure.java.io" "clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["util"],
                              :lib-deps ["clojure"]}
                             {:name "create",
                              :type "component",
                              :lines-of-code-src 181,
                              :lines-of-code-test 282,
                              :interface {:name "create",
                                          :definitions [{:name "create-base",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "base-name"}]}
                                                        {:name "create-component",
                                                         :type "function",
                                                         :parameters [{:name "workspace"}
                                                                      {:name "component-name"}
                                                                      {:name "interface-name"}]}
                                                        {:name "create-environment",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "env"}]}
                                                        {:name "create-workspace",
                                                         :type "function",
                                                         :parameters [{:name "root-dir"} {:name "ws-name"} {:name "top-ns"}]}
                                                        {:name "print-alias-message",
                                                         :type "function",
                                                         :parameters [{:name "env"} {:name "color-mode"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.create.interfc",
                                                :file-path "./components/create/src/polylith/clj/core/create/interfc.clj",
                                                :imports ["polylith.clj.core.create.base"
                                                          "polylith.clj.core.create.component"
                                                          "polylith.clj.core.create.environment"
                                                          "polylith.clj.core.create.workspace"]}
                                               {:name "brick",
                                                :namespace "polylith.clj.core.create.brick",
                                                :file-path "./components/create/src/polylith/clj/core/create/brick.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.git.interfc"]}
                                               {:name "environment",
                                                :namespace "polylith.clj.core.create.environment",
                                                :file-path "./components/create/src/polylith/clj/core/create/environment.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.git.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "base",
                                                :namespace "polylith.clj.core.create.base",
                                                :file-path "./components/create/src/polylith/clj/core/create/base.clj",
                                                :imports ["polylith.clj.core.create.brick"]}
                                               {:name "workspace",
                                                :namespace "polylith.clj.core.create.workspace",
                                                :file-path "./components/create/src/polylith/clj/core/create/workspace.clj",
                                                :imports ["polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.git.interfc"
                                                          "polylith.clj.core.user-config.interfc"]}
                                               {:name "component",
                                                :namespace "polylith.clj.core.create.component",
                                                :file-path "./components/create/src/polylith/clj/core/create/component.clj",
                                                :imports ["polylith.clj.core.create.brick"]}],
                              :namespaces-test [{:name "base-test",
                                                 :namespace "polylith.clj.core.create.base-test",
                                                 :file-path "./components/create/test/polylith/clj/core/create/base_test.clj",
                                                 :imports ["polylith.clj.core.create.brick" "polylith.clj.core.test-helper.interfc"]}
                                                {:name "workspace-test",
                                                 :namespace "polylith.clj.core.create.workspace-test",
                                                 :file-path "./components/create/test/polylith/clj/core/create/workspace_test.clj",
                                                 :imports ["polylith.clj.core.test-helper.interfc"]}
                                                {:name "environment-test",
                                                 :namespace "polylith.clj.core.create.environment-test",
                                                 :file-path "./components/create/test/polylith/clj/core/create/environment_test.clj",
                                                 :imports ["polylith.clj.core.test-helper.interfc"
                                                           "polylith.clj.core.util.interfc.color"]}
                                                {:name "component-test",
                                                 :namespace "polylith.clj.core.create.component-test",
                                                 :file-path "./components/create/test/polylith/clj/core/create/component_test.clj",
                                                 :imports ["polylith.clj.core.create.brick" "polylith.clj.core.test-helper.interfc"]}],
                              :lib-imports-src [],
                              :lib-imports-test [],
                              :interface-deps ["common" "file" "git" "user-config" "util"],
                              :lib-deps []}
                             {:name "deps",
                              :type "component",
                              :lines-of-code-src 242,
                              :lines-of-code-test 328,
                              :interface {:name "deps",
                                          :definitions [{:name "interface-deps",
                                                         :type "function",
                                                         :parameters [{:name "suffixed-top-ns"}
                                                                      {:name "interface-names"}
                                                                      {:name "brick"}]}
                                                        {:name "interface-ns-deps",
                                                         :type "function",
                                                         :parameters [{:name "top-ns"}
                                                                      {:name "interface-name"}
                                                                      {:name "interface-names"}
                                                                      {:name "brick-namespaces"}]}
                                                        {:name "print-brick-ifc-table",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "brick-name"} {:name "color-mode"}]}
                                                        {:name "print-brick-table",
                                                         :type "function",
                                                         :parameters [{:name "workspace"}
                                                                      {:name "environment-name"}
                                                                      {:name "brick-name"}
                                                                      {:name "color-mode"}]}
                                                        {:name "print-workspace-brick-table",
                                                         :type "function",
                                                         :parameters [{:name "workspace"}
                                                                      {:name "environment-name"}
                                                                      {:name "color-mode"}]}
                                                        {:name "print-workspace-ifc-table",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "color-mode"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.deps.interfc",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/interfc.clj",
                                                :imports ["polylith.clj.core.deps.interface-deps"
                                                          "polylith.clj.core.deps.text-table.brick-deps-table"
                                                          "polylith.clj.core.deps.text-table.brick-ifc-deps-table"
                                                          "polylith.clj.core.deps.text-table.workspace-brick-deps-table"
                                                          "polylith.clj.core.deps.text-table.workspace-ifc-deps-table"]}
                                               {:name "text-table.workspace-ifc-deps-table",
                                                :namespace "polylith.clj.core.deps.text-table.workspace-ifc-deps-table",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/text_table/workspace_ifc_deps_table.clj",
                                                :imports ["polylith.clj.core.text-table.interfc"]}
                                               {:name "text-table.brick-deps-table",
                                                :namespace "polylith.clj.core.deps.text-table.brick-deps-table",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/text_table/brick_deps_table.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.deps.brick-deps"
                                                          "polylith.clj.core.text-table.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "text-table.brick-ifc-deps-table",
                                                :namespace "polylith.clj.core.deps.text-table.brick-ifc-deps-table",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/text_table/brick_ifc_deps_table.clj",
                                                :imports ["polylith.clj.core.common.interfc" "polylith.clj.core.text-table.interfc"]}
                                               {:name "text-table.workspace-brick-deps-table",
                                                :namespace "polylith.clj.core.deps.text-table.workspace-brick-deps-table",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/text_table/workspace_brick_deps_table.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.text-table.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "interface-deps",
                                                :namespace "polylith.clj.core.deps.interface-deps",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/interface_deps.clj",
                                                :imports ["clojure.string"]}
                                               {:name "brick-deps",
                                                :namespace "polylith.clj.core.deps.brick-deps",
                                                :file-path "./components/deps/src/polylith/clj/core/deps/brick_deps.clj",
                                                :imports ["clojure.set"]}],
                              :namespaces-test [{:name "brick-deps-test",
                                                 :namespace "polylith.clj.core.deps.brick-deps-test",
                                                 :file-path "./components/deps/test/polylith/clj/core/deps/brick_deps_test.clj",
                                                 :imports ["polylith.clj.core.deps.brick-deps"]}
                                                {:name "interface-deps-test",
                                                 :namespace "polylith.clj.core.deps.interface-deps-test",
                                                 :file-path "./components/deps/test/polylith/clj/core/deps/interface_deps_test.clj",
                                                 :imports ["polylith.clj.core.deps.interface-deps"]}
                                                {:name "text-table.brick-ifc-deps-table-test",
                                                 :namespace "polylith.clj.core.deps.text-table.brick-ifc-deps-table-test",
                                                 :file-path "./components/deps/test/polylith/clj/core/deps/text_table/brick_ifc_deps_table_test.clj",
                                                 :imports ["clojure.string"
                                                           "polylith.clj.core.deps.text-table.brick-ifc-deps-table"
                                                           "polylith.clj.core.util.interfc.color"]}
                                                {:name "text-table.workspace-brick-deps-table-test",
                                                 :namespace "polylith.clj.core.deps.text-table.workspace-brick-deps-table-test",
                                                 :file-path "./components/deps/test/polylith/clj/core/deps/text_table/workspace_brick_deps_table_test.clj",
                                                 :imports ["clojure.string"
                                                           "polylith.clj.core.deps.text-table.workspace-brick-deps-table"
                                                           "polylith.clj.core.util.interfc.color"]}
                                                {:name "text-table.workspace-ifc-deps-table-test",
                                                 :namespace "polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test",
                                                 :file-path "./components/deps/test/polylith/clj/core/deps/text_table/workspace_ifc_deps_table_test.clj",
                                                 :imports ["clojure.string"
                                                           "polylith.clj.core.deps.text-table.workspace-ifc-deps-table"
                                                           "polylith.clj.core.util.interfc.color"]}
                                                {:name "text-table.brick-deps-table-test",
                                                 :namespace "polylith.clj.core.deps.text-table.brick-deps-table-test",
                                                 :file-path "./components/deps/test/polylith/clj/core/deps/text_table/brick_deps_table_test.clj",
                                                 :imports ["clojure.string"
                                                           "polylith.clj.core.deps.text-table.brick-deps-table"
                                                           "polylith.clj.core.util.interfc.color"]}],
                              :lib-imports-src ["clojure.set" "clojure.string"],
                              :lib-imports-test ["clojure.string"],
                              :interface-deps ["common" "text-table" "util"],
                              :lib-deps ["clojure"]}
                             {:name "entity",
                              :type "component",
                              :lines-of-code-src 591,
                              :lines-of-code-test 343,
                              :interface {:name "entity",
                                          :definitions [{:name "all-src-deps", :type "function", :parameters [{:name "dep-entries"}]}
                                                        {:name "all-test-deps", :type "function", :parameters [{:name "dep-entries"}]}
                                                        {:name "brick-status-flags",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"} {:name "brick-name"}]}
                                                        {:name "deps-entries",
                                                         :type "function",
                                                         :parameters [{:name "dev?"}
                                                                      {:name "src-deps"}
                                                                      {:name "test-deps"}
                                                                      {:name "settings"}]}
                                                        {:name "env-status-flags",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"} {:name "env-name"}]}
                                                        {:name "path-entries",
                                                         :type "function",
                                                         :parameters [{:name "ws-dir"}
                                                                      {:name "dev?"}
                                                                      {:name "src-paths"}
                                                                      {:name "test-paths"}
                                                                      {:name "settings"}]}
                                                        {:name "src-base-names",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"}]}
                                                        {:name "src-brick-names",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"}]}
                                                        {:name "src-component-names",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"}]}
                                                        {:name "src-paths", :type "function", :parameters [{:name "path-entries"}]}
                                                        {:name "test-base-names",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"}]}
                                                        {:name "test-component-names",
                                                         :type "function",
                                                         :parameters [{:name "path-entries"}]}
                                                        {:name "test-paths", :type "function", :parameters [{:name "path-entries"}]}]},
                              :namespaces-src [{:name "profile-extractor",
                                                :namespace "polylith.clj.core.entity.profile-extractor",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/profile_extractor.clj",
                                                :imports ["clojure.string"]}
                                               {:name "path-extractor",
                                                :namespace "polylith.clj.core.entity.path-extractor",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/path_extractor.clj",
                                                :imports ["polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.str"]}
                                               {:name "dep-selector",
                                                :namespace "polylith.clj.core.entity.dep-selector",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/dep_selector.clj",
                                                :imports ["polylith.clj.core.entity.matchers"]}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.entity.interfc",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/interfc.clj",
                                                :imports ["polylith.clj.core.entity.core"
                                                          "polylith.clj.core.entity.dep-selector"
                                                          "polylith.clj.core.entity.path-selector"
                                                          "polylith.clj.core.entity.status"]}
                                               {:name "matchers",
                                                :namespace "polylith.clj.core.entity.matchers",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/matchers.clj",
                                                :imports ["clojure.string"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.entity.core",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/core.clj",
                                                :imports ["polylith.clj.core.entity.dep-extractor"
                                                          "polylith.clj.core.entity.path-extractor"
                                                          "polylith.clj.core.entity.profile-extractor"]}
                                               {:name "env-statuses",
                                                :namespace "polylith.clj.core.entity.env-statuses",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/env_statuses.clj",
                                                :imports ["polylith.clj.core.entity.matchers"]}
                                               {:name "dep-extractor",
                                                :namespace "polylith.clj.core.entity.dep-extractor",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/dep_extractor.clj",
                                                :imports ["polylith.clj.core.util.interfc"]}
                                               {:name "path-selector",
                                                :namespace "polylith.clj.core.entity.path-selector",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/path_selector.clj",
                                                :imports ["polylith.clj.core.entity.matchers"]}
                                               {:name "status",
                                                :namespace "polylith.clj.core.entity.status",
                                                :file-path "./components/entity/src/polylith/clj/core/entity/status.clj",
                                                :imports ["polylith.clj.core.entity.matchers"]}],
                              :namespaces-test [{:name "dep-selector-test",
                                                 :namespace "polylith.clj.core.entity.dep-selector-test",
                                                 :file-path "./components/entity/test/polylith/clj/core/entity/dep_selector_test.clj",
                                                 :imports ["polylith.clj.core.entity.dep-selector"
                                                           "polylith.clj.core.entity.test-data"]}
                                                {:name "path-selector-test",
                                                 :namespace "polylith.clj.core.entity.path-selector-test",
                                                 :file-path "./components/entity/test/polylith/clj/core/entity/path_selector_test.clj",
                                                 :imports ["polylith.clj.core.entity.path-selector"
                                                           "polylith.clj.core.entity.test-data"]}
                                                {:name "test-data",
                                                 :namespace "polylith.clj.core.entity.test-data",
                                                 :file-path "./components/entity/test/polylith/clj/core/entity/test_data.clj",
                                                 :imports []}
                                                {:name "path-extractor-test",
                                                 :namespace "polylith.clj.core.entity.path-extractor-test",
                                                 :file-path "./components/entity/test/polylith/clj/core/entity/path_extractor_test.clj",
                                                 :imports ["polylith.clj.core.entity.path-extractor"
                                                           "polylith.clj.core.entity.test-data"
                                                           "polylith.clj.core.file.interfc"]}
                                                {:name "dep-extractor-test",
                                                 :namespace "polylith.clj.core.entity.dep-extractor-test",
                                                 :file-path "./components/entity/test/polylith/clj/core/entity/dep_extractor_test.clj",
                                                 :imports ["polylith.clj.core.entity.dep-extractor"
                                                           "polylith.clj.core.entity.test-data"]}
                                                {:name "profile-extractor-test",
                                                 :namespace "polylith.clj.core.entity.profile-extractor-test",
                                                 :file-path "./components/entity/test/polylith/clj/core/entity/profile_extractor_test.clj",
                                                 :imports ["polylith.clj.core.entity.profile-extractor"]}],
                              :lib-imports-src ["clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["file" "util"],
                              :lib-deps ["clojure"]}
                             {:name "file",
                              :type "component",
                              :lines-of-code-src 165,
                              :lines-of-code-test 2,
                              :interface {:name "file",
                                          :definitions [{:name "absolute-path", :type "function", :parameters [{:name "path"}]}
                                                        {:name "copy-resource-file!",
                                                         :type "function",
                                                         :parameters [{:name "source"} {:name "target-path"}]}
                                                        {:name "create-dir",
                                                         :type "function",
                                                         :parameters [{:name "path", :type "^String"}]}
                                                        {:name "create-file",
                                                         :type "function",
                                                         :parameters [{:name "path"} {:name "rows"}]}
                                                        {:name "create-missing-dirs",
                                                         :type "function",
                                                         :parameters [{:name "filename"}]}
                                                        {:name "create-temp-dir", :type "function", :parameters [{:name "dir"}]}
                                                        {:name "current-dir", :type "function", :parameters []}
                                                        {:name "delete-dir", :type "function", :parameters [{:name "path"}]}
                                                        {:name "delete-file", :type "function", :parameters [{:name "path"}]}
                                                        {:name "delete-folder", :type "function", :parameters [{:name "file"}]}
                                                        {:name "directory-paths", :type "function", :parameters [{:name "dir"}]}
                                                        {:name "directory?",
                                                         :type "function",
                                                         :parameters [{:name "file", :type "^File"}]}
                                                        {:name "exists", :type "function", :parameters [{:name "path"}]}
                                                        {:name "file-name",
                                                         :type "function",
                                                         :parameters [{:name "file", :type "^File"}]}
                                                        {:name "files-recursively", :type "function", :parameters [{:name "dir-path"}]}
                                                        {:name "lines-of-code", :type "function", :parameters [{:name "file-path"}]}
                                                        {:name "paths-recursively", :type "function", :parameters [{:name "dir"}]}
                                                        {:name "read-file", :type "function", :parameters [{:name "path"}]}
                                                        {:name "relative-paths", :type "function", :parameters [{:name "path"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.file.interfc",
                                                :file-path "./components/file/src/polylith/clj/core/file/interfc.clj",
                                                :imports ["polylith.clj.core.file.core"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.file.core",
                                                :file-path "./components/file/src/polylith/clj/core/file/core.clj",
                                                :imports ["clojure.java.io" "polylith.clj.core.util.interfc.str"]}],
                              :namespaces-test [{:name "core-test",
                                                 :namespace "polylith.clj.core.file.core-test",
                                                 :file-path "./components/file/test/polylith/clj/core/file/core_test.clj",
                                                 :imports []}],
                              :lib-imports-src ["clojure.java.io"],
                              :lib-imports-test [],
                              :interface-deps ["util"],
                              :lib-deps ["clojure"]}
                             {:name "git",
                              :type "component",
                              :lines-of-code-src 55,
                              :lines-of-code-test 18,
                              :interface {:name "git",
                                          :definitions [{:name "add",
                                                         :type "function",
                                                         :parameters [{:name "ws-dir"} {:name "filename"}]}
                                                        {:name "current-sha", :type "function", :parameters [{:name "ws-dir"}]}
                                                        {:name "diff",
                                                         :type "function",
                                                         :parameters [{:name "ws-dir"} {:name "sha1"} {:name "sha2"}]}
                                                        {:name "diff-command",
                                                         :type "function",
                                                         :parameters [{:name "sha1"} {:name "sha2"}]}
                                                        {:name "init", :type "function", :parameters [{:name "ws-dir"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.git.interfc",
                                                :file-path "./components/git/src/polylith/clj/core/git/interfc.clj",
                                                :imports ["polylith.clj.core.git.core"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.git.core",
                                                :file-path "./components/git/src/polylith/clj/core/git/core.clj",
                                                :imports ["clojure.string" "polylith.clj.core.shell.interfc"]}],
                              :namespaces-test [{:name "git-test",
                                                 :namespace "polylith.clj.core.git.git-test",
                                                 :file-path "./components/git/test/polylith/clj/core/git/git_test.clj",
                                                 :imports ["polylith.clj.core.git.interfc"]}],
                              :lib-imports-src ["clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["shell"],
                              :lib-deps ["clojure"]}
                             {:name "help",
                              :type "component",
                              :lines-of-code-src 204,
                              :lines-of-code-test 0,
                              :interface {:name "help",
                                          :definitions [{:name "print-help",
                                                         :type "function",
                                                         :parameters [{:name "cmd"} {:name "color-mode"}]}]},
                              :namespaces-src [{:name "deps",
                                                :namespace "polylith.clj.core.help.deps",
                                                :file-path "./components/help/src/polylith/clj/core/help/deps.clj",
                                                :imports []}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.help.interfc",
                                                :file-path "./components/help/src/polylith/clj/core/help/interfc.clj",
                                                :imports ["polylith.clj.core.help.core"]}
                                               {:name "check",
                                                :namespace "polylith.clj.core.help.check",
                                                :file-path "./components/help/src/polylith/clj/core/help/check.clj",
                                                :imports ["polylith.clj.core.help.shared" "polylith.clj.core.util.interfc.color"]}
                                               {:name "info",
                                                :namespace "polylith.clj.core.help.info",
                                                :file-path "./components/help/src/polylith/clj/core/help/info.clj",
                                                :imports ["polylith.clj.core.help.shared" "polylith.clj.core.util.interfc.color"]}
                                               {:name "shared",
                                                :namespace "polylith.clj.core.help.shared",
                                                :file-path "./components/help/src/polylith/clj/core/help/shared.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.interfc.color"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.help.core",
                                                :file-path "./components/help/src/polylith/clj/core/help/core.clj",
                                                :imports ["polylith.clj.core.help.check"
                                                          "polylith.clj.core.help.deps"
                                                          "polylith.clj.core.help.info"
                                                          "polylith.clj.core.help.summary"
                                                          "polylith.clj.core.help.test"]}
                                               {:name "summary",
                                                :namespace "polylith.clj.core.help.summary",
                                                :file-path "./components/help/src/polylith/clj/core/help/summary.clj",
                                                :imports ["polylith.clj.core.util.interfc.color"]}
                                               {:name "test",
                                                :namespace "polylith.clj.core.help.test",
                                                :file-path "./components/help/src/polylith/clj/core/help/test.clj",
                                                :imports ["polylith.clj.core.util.interfc.color"]}],
                              :namespaces-test [],
                              :lib-imports-src ["clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["util"],
                              :lib-deps ["clojure"]}
                             {:name "shell",
                              :type "component",
                              :lines-of-code-src 19,
                              :lines-of-code-test 0,
                              :interface {:name "shell",
                                          :definitions [{:name "sh", :type "function", :parameters [{:name "&"} {:name "args"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.shell.interfc",
                                                :file-path "./components/shell/src/polylith/clj/core/shell/interfc.clj",
                                                :imports ["polylith.clj.core.shell.core"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.shell.core",
                                                :file-path "./components/shell/src/polylith/clj/core/shell/core.clj",
                                                :imports ["clojure.java.shell"]}],
                              :namespaces-test [],
                              :lib-imports-src ["clojure.java.shell"],
                              :lib-imports-test [],
                              :interface-deps [],
                              :lib-deps ["clojure"]}
                             {:name "test-helper",
                              :type "component",
                              :lines-of-code-src 73,
                              :lines-of-code-test 0,
                              :interface {:name "test-helper",
                                          :definitions [{:name "content",
                                                         :type "function",
                                                         :parameters [{:name "dir"} {:name "filename"}]}
                                                        {:name "execute-command",
                                                         :type "function",
                                                         :parameters [{:name "current-dir"}
                                                                      {:name "cmd"}
                                                                      {:name "&"}
                                                                      {:name "[arg1 arg2 arg3]"}]}
                                                        {:name "paths", :type "function", :parameters [{:name "dir"}]}
                                                        {:name "root-dir", :type "function", :parameters []}
                                                        {:name "test-setup-and-tear-down",
                                                         :type "function",
                                                         :parameters [{:name "function"}]}
                                                        {:name "user-home", :type "function", :parameters []}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.test-helper.interfc",
                                                :file-path "./components/test-helper/src/polylith/clj/core/test_helper/interfc.clj",
                                                :imports ["polylith.clj.core.test-helper.core"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.test-helper.core",
                                                :file-path "./components/test-helper/src/polylith/clj/core/test_helper/core.clj",
                                                :imports ["clojure.stacktrace"
                                                          "clojure.string"
                                                          "polylith.clj.core.change.interfc"
                                                          "polylith.clj.core.command.interfc"
                                                          "polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.git.interfc"
                                                          "polylith.clj.core.user-config.interfc"
                                                          "polylith.clj.core.workspace-clj.interfc"
                                                          "polylith.clj.core.workspace.interfc"]}],
                              :namespaces-test [],
                              :lib-imports-src ["clojure.stacktrace" "clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["change" "command" "common" "file" "git" "user-config" "workspace" "workspace-clj"],
                              :lib-deps ["clojure"]}
                             {:name "test-runner",
                              :type "component",
                              :lines-of-code-src 108,
                              :lines-of-code-test 0,
                              :interface {:name "test-runner",
                                          :definitions [{:name "run", :type "function", :parameters [{:name "workspace"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.test-runner.interfc",
                                                :file-path "./components/test-runner/src/polylith/clj/core/test_runner/interfc.clj",
                                                :imports ["polylith.clj.core.test-runner.core"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.test-runner.core",
                                                :file-path "./components/test-runner/src/polylith/clj/core/test_runner/core.clj",
                                                :imports ["clojure.string"
                                                          "clojure.tools.deps.alpha"
                                                          "polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.util.interfc.str"
                                                          "polylith.clj.core.util.interfc.time"]}],
                              :namespaces-test [],
                              :lib-imports-src ["clojure.string" "clojure.tools.deps.alpha"],
                              :lib-imports-test [],
                              :interface-deps ["common" "util"],
                              :lib-deps ["clojure" "clojure.tools.deps"]}
                             {:name "text-table",
                              :type "component",
                              :lines-of-code-src 145,
                              :lines-of-code-test 117,
                              :interface {:name "text-table",
                                          :definitions [{:name "line", :type "function", :parameters [{:name "rows"}]}
                                                        {:name "line",
                                                         :type "function",
                                                         :parameters [{:name "rows"} {:name "visables"}]}
                                                        {:name "table",
                                                         :type "function",
                                                         :parameters [{:name "initial-spaces"}
                                                                      {:name "alignments"}
                                                                      {:name "colors"}
                                                                      {:name "rows"}
                                                                      {:name "color-mode"}]}
                                                        {:name "table",
                                                         :type "function",
                                                         :parameters [{:name "initial-spaces"}
                                                                      {:name "alignments"}
                                                                      {:name "header-colors"}
                                                                      {:name "header-orientations"}
                                                                      {:name "colors"}
                                                                      {:name "headers"}
                                                                      {:name "rows"}
                                                                      {:name "color-mode"}]}
                                                        {:name "table",
                                                         :type "function",
                                                         :parameters [{:name "initial-spaces"}
                                                                      {:name "alignments"}
                                                                      {:name "header-colors"}
                                                                      {:name "header-orientations"}
                                                                      {:name "colors"}
                                                                      {:name "headers"}
                                                                      {:name "line-visables"}
                                                                      {:name "rows"}
                                                                      {:name "color-mode"}]}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.text-table.interfc",
                                                :file-path "./components/text-table/src/polylith/clj/core/text_table/interfc.clj",
                                                :imports ["polylith.clj.core.text-table.core" "polylith.clj.core.text-table.line"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.text-table.core",
                                                :file-path "./components/text-table/src/polylith/clj/core/text_table/core.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.text-table.line"
                                                          "polylith.clj.core.text-table.orientation"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.util.interfc.str"]}
                                               {:name "line",
                                                :namespace "polylith.clj.core.text-table.line",
                                                :file-path "./components/text-table/src/polylith/clj/core/text_table/line.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.interfc.color"]}
                                               {:name "orientation",
                                                :namespace "polylith.clj.core.text-table.orientation",
                                                :file-path "./components/text-table/src/polylith/clj/core/text_table/orientation.clj",
                                                :imports ["clojure.core.matrix"]}],
                              :namespaces-test [{:name "interfc-test",
                                                 :namespace "polylith.clj.core.text-table.interfc-test",
                                                 :file-path "./components/text-table/test/polylith/clj/core/text_table/interfc_test.clj",
                                                 :imports ["clojure.string"
                                                           "polylith.clj.core.text-table.interfc"
                                                           "polylith.clj.core.util.interfc.color"]}],
                              :lib-imports-src ["clojure.core.matrix" "clojure.string"],
                              :lib-imports-test ["clojure.string"],
                              :interface-deps ["util"],
                              :lib-deps ["clojure" "clojure.core.matrix"]}

                             {:name "user-config",
                              :type "component",
                              :lines-of-code-src 18,
                              :lines-of-code-test 0,
                              :interface {:name "user-config",
                                          :definitions [{:name "color-mode", :type "function", :parameters []}
                                                        {:name "config-content", :type "function", :parameters []}
                                                        {:name "home-dir", :type "function", :parameters []}
                                                        {:name "thousand-separator", :type "function", :parameters []}]},
                              :namespaces-src [{:name "interfc",
                                                :namespace "polylith.clj.core.user-config.interfc",
                                                :file-path "./components/user-config/src/polylith/clj/core/user_config/interfc.clj",
                                                :imports ["polylith.clj.core.util.interfc.str"]}],
                              :namespaces-test [],
                              :lib-imports-src [],
                              :lib-imports-test [],
                              :interface-deps ["util"],
                              :lib-deps []}
                             {:name "util",
                              :type "component",
                              :lines-of-code-src 290,
                              :lines-of-code-test 64,
                              :interface {:name "util",
                                          :definitions [{:name "find-first",
                                                         :type "function",
                                                         :parameters [{:name "predicate"} {:name "sequence"}]}
                                                        {:name "first-as-vector", :type "function", :parameters [{:name "vals"}]}
                                                        {:name "ordered-map",
                                                         :type "function",
                                                         :parameters [{:name "&"} {:name "keyvals"}]}
                                                        {:name "stringify-and-sort-map", :type "function", :parameters [{:name "m"}]}
                                                        {:name "def-keys", :type "macro", :parameters [{:name "amap"} {:name "keys"}]}
                                                        {:name "none", :type "data", :sub-ns "color"}
                                                        {:name "base",
                                                         :type "function",
                                                         :parameters [{:name "base"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "blue",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "brick",
                                                         :type "function",
                                                         :parameters [{:name "type"} {:name "brick"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "clean-colors",
                                                         :type "function",
                                                         :parameters [{:name "message"}],
                                                         :sub-ns "color"}
                                                        {:name "colored-text",
                                                         :type "function",
                                                         :parameters [{:name "color"} {:name "color-mode"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "colored-text",
                                                         :type "function",
                                                         :parameters [{:name "color-light"}
                                                                      {:name "color-dark"}
                                                                      {:name "color-mode"}
                                                                      {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "component",
                                                         :type "function",
                                                         :parameters [{:name "component"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "cyan",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "environment",
                                                         :type "function",
                                                         :parameters [{:name "env"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "error",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "green",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "grey",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "interface",
                                                         :type "function",
                                                         :parameters [{:name "ifc"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "namespc",
                                                         :type "function",
                                                         :parameters [{:name "namespace"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "namespc",
                                                         :type "function",
                                                         :parameters [{:name "interface"} {:name "namespace"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "ok",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "purple",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "red",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "warning",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "yellow",
                                                         :type "function",
                                                         :parameters [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "print-error-message",
                                                         :type "function",
                                                         :parameters [{:name "e"}],
                                                         :sub-ns "exception"}
                                                        {:name "print-exception",
                                                         :type "function",
                                                         :parameters [{:name "e"}],
                                                         :sub-ns "exception"}
                                                        {:name "print-stacktrace",
                                                         :type "function",
                                                         :parameters [{:name "e"}],
                                                         :sub-ns "exception"}
                                                        {:name "extract",
                                                         :type "function",
                                                         :parameters [{:name "args"}],
                                                         :sub-ns "params"}
                                                        {:name "key-name",
                                                         :type "function",
                                                         :parameters [{:name "arg"}],
                                                         :sub-ns "params"}
                                                        {:name "named?",
                                                         :type "function",
                                                         :parameters [{:name "arg"}],
                                                         :sub-ns "params"}
                                                        {:name "unnamed?",
                                                         :type "function",
                                                         :parameters [{:name "arg"}],
                                                         :sub-ns "params"}
                                                        {:name "count-things",
                                                         :type "function",
                                                         :parameters [{:name "thing"} {:name "cnt"}],
                                                         :sub-ns "str"}
                                                        {:name "line", :type "function", :parameters [{:name "length"}], :sub-ns "str"}
                                                        {:name "sep-1000",
                                                         :type "function",
                                                         :parameters [{:name "number"} {:name "sep"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-if-ends-with",
                                                         :type "function",
                                                         :parameters [{:name "string"} {:name "ends-with"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-prefix",
                                                         :type "function",
                                                         :parameters [{:name "string"} {:name "prefix"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-suffix",
                                                         :type "function",
                                                         :parameters [{:name "string"} {:name "suffix"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-suffixes",
                                                         :type "function",
                                                         :parameters [{:name "string"} {:name "suffixes"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-until",
                                                         :type "function",
                                                         :parameters [{:name "string"} {:name "separator"}],
                                                         :sub-ns "str"}
                                                        {:name "spaces",
                                                         :type "function",
                                                         :parameters [{:name "n#spaces"}],
                                                         :sub-ns "str"}
                                                        {:name "take-until",
                                                         :type "function",
                                                         :parameters [{:name "string"} {:name "separator"}],
                                                         :sub-ns "str"}
                                                        {:name "current-time", :type "function", :parameters [], :sub-ns "time"}
                                                        {:name "print-execution-time",
                                                         :type "function",
                                                         :parameters [{:name "start-time"}],
                                                         :sub-ns "time"}]},
                              :namespaces-src [{:name "str",
                                                :namespace "polylith.clj.core.util.str",
                                                :file-path "./components/util/src/polylith/clj/core/util/str.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.core"]}
                                               {:name "time",
                                                :namespace "polylith.clj.core.util.time",
                                                :file-path "./components/util/src/polylith/clj/core/util/time.clj",
                                                :imports []}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.util.interfc",
                                                :file-path "./components/util/src/polylith/clj/core/util/interfc.clj",
                                                :imports ["polylith.clj.core.util.core"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.util.core",
                                                :file-path "./components/util/src/polylith/clj/core/util/core.clj",
                                                :imports []}
                                               {:name "interfc.str",
                                                :namespace "polylith.clj.core.util.interfc.str",
                                                :file-path "./components/util/src/polylith/clj/core/util/interfc/str.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.str"]}
                                               {:name "interfc.time",
                                                :namespace "polylith.clj.core.util.interfc.time",
                                                :file-path "./components/util/src/polylith/clj/core/util/interfc/time.clj",
                                                :imports ["polylith.clj.core.util.time"]}
                                               {:name "interfc.params",
                                                :namespace "polylith.clj.core.util.interfc.params",
                                                :file-path "./components/util/src/polylith/clj/core/util/interfc/params.clj",
                                                :imports ["clojure.string"]}
                                               {:name "interfc.exception",
                                                :namespace "polylith.clj.core.util.interfc.exception",
                                                :file-path "./components/util/src/polylith/clj/core/util/interfc/exception.clj",
                                                :imports ["clojure.stacktrace"]}
                                               {:name "interfc.color",
                                                :namespace "polylith.clj.core.util.interfc.color",
                                                :file-path "./components/util/src/polylith/clj/core/util/interfc/color.clj",
                                                :imports ["clojure.string"]}],
                              :namespaces-test [{:name "util-test",
                                                 :namespace "polylith.clj.core.util.util-test",
                                                 :file-path "./components/util/test/polylith/clj/core/util/util_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc"]}
                                                {:name "params-test",
                                                 :namespace "polylith.clj.core.util.params-test",
                                                 :file-path "./components/util/test/polylith/clj/core/util/params_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.params"]}
                                                {:name "string-util-test",
                                                 :namespace "polylith.clj.core.util.string-util-test",
                                                 :file-path "./components/util/test/polylith/clj/core/util/string_util_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.str"]}],
                              :lib-imports-src ["clojure.stacktrace" "clojure.string"],
                              :lib-imports-test [],
                              :interface-deps [],
                              :lib-deps ["clojure"]}
                             {:name "validate",
                              :type "component",
                              :lines-of-code-src 420,
                              :lines-of-code-test 810,
                              :interface {:name "validate",
                                          :definitions [{:name "messages",
                                                         :type "function",
                                                         :parameters [{:name "ws-dir"}
                                                                      {:name "suffixed-top-ns"}
                                                                      {:name "interface-names"}
                                                                      {:name "interfaces"}
                                                                      {:name "components"}
                                                                      {:name "bases"}
                                                                      {:name "environments"}
                                                                      {:name "interface-ns"}
                                                                      {:name "ns->lib"}
                                                                      {:name "color-mode"}]}]},
                              :namespaces-src [{:name "m104-circular-deps",
                                                :namespace "polylith.clj.core.validate.m104-circular-deps",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m104_circular_deps.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "m105-illegal-name-sharing",
                                                :namespace "polylith.clj.core.validate.m105-illegal-name-sharing",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m105_illegal_name_sharing.clj",
                                                :imports ["clojure.set"
                                                          "clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "m107-missing-componens-in-environment",
                                                :namespace "polylith.clj.core.validate.m107-missing-componens-in-environment",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m107_missing_componens_in_environment.clj",
                                                :imports ["clojure.set"
                                                          "clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "m102-function-or-macro-is-defined-twice",
                                                :namespace "polylith.clj.core.validate.m102-function-or-macro-is-defined-twice",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m102_function_or_macro_is_defined_twice.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.validate.shared"]}
                                               {:name "m201-mismatching-parameters",
                                                :namespace "polylith.clj.core.validate.m201-mismatching-parameters",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m201_mismatching_parameters.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.validate.shared"]}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.validate.interfc",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/interfc.clj",
                                                :imports ["polylith.clj.core.validate.m101-illegal-namespace-deps"
                                                          "polylith.clj.core.validate.m102-function-or-macro-is-defined-twice"
                                                          "polylith.clj.core.validate.m103-missing-defs"
                                                          "polylith.clj.core.validate.m104-circular-deps"
                                                          "polylith.clj.core.validate.m105-illegal-name-sharing"
                                                          "polylith.clj.core.validate.m106-multiple-interface-occurrences"
                                                          "polylith.clj.core.validate.m107-missing-componens-in-environment"
                                                          "polylith.clj.core.validate.m201-mismatching-parameters"
                                                          "polylith.clj.core.validate.m202-missing-libraries"
                                                          "polylith.clj.core.validate.m203-invalid-src-reference"]}
                                               {:name "m202-missing-libraries",
                                                :namespace "polylith.clj.core.validate.m202-missing-libraries",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m202_missing_libraries.clj",
                                                :imports ["clojure.set"
                                                          "clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "m106-multiple-interface-occurrences",
                                                :namespace "polylith.clj.core.validate.m106-multiple-interface-occurrences",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m106_multiple_interface_occurrences.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "m101-illegal-namespace-deps",
                                                :namespace "polylith.clj.core.validate.m101-illegal-namespace-deps",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m101_illegal_namespace_deps.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.deps.interfc"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"]}
                                               {:name "shared",
                                                :namespace "polylith.clj.core.validate.shared",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/shared.clj",
                                                :imports ["clojure.string"]}
                                               {:name "m103-missing-defs",
                                                :namespace "polylith.clj.core.validate.m103-missing-defs",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m103_missing_defs.clj",
                                                :imports ["clojure.set"
                                                          "clojure.string"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.validate.shared"]}
                                               {:name "m203-invalid-src-reference",
                                                :namespace "polylith.clj.core.validate.m203-invalid-src-reference",
                                                :file-path "./components/validate/src/polylith/clj/core/validate/m203_invalid_src_reference.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.util.interfc.color"
                                                          "polylith.clj.core.util.interfc.str"]}],
                              :namespaces-test [{:name "m102-duplicated-parameter-lists-test",
                                                 :namespace "polylith.clj.core.validate.m102-duplicated-parameter-lists-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m102_duplicated_parameter_lists_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m102-function-or-macro-is-defined-twice"]}
                                                {:name "m101-illegal-namespace-deps-test",
                                                 :namespace "polylith.clj.core.validate.m101-illegal-namespace-deps-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m101_illegal_namespace_deps_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m101-illegal-namespace-deps"]}
                                                {:name "m203-invalid-src-reference-test",
                                                 :namespace "polylith.clj.core.validate.m203-invalid-src-reference-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m203_invalid_src_reference_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m203-invalid-src-reference"]}
                                                {:name "m105-illegal-name-sharing-test",
                                                 :namespace "polylith.clj.core.validate.m105-illegal-name-sharing-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m105_illegal_name_sharing_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m105-illegal-name-sharing"]}
                                                {:name "m104-circular-deps-test",
                                                 :namespace "polylith.clj.core.validate.m104-circular-deps-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m104_circular_deps_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m104-circular-deps"]}
                                                {:name "m106-multiple-interface-occurrences-test",
                                                 :namespace "polylith.clj.core.validate.m106-multiple-interface-occurrences-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m106_multiple_interface_occurrences_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m106-multiple-interface-occurrences"]}
                                                {:name "m201-mismatching-parameters-test",
                                                 :namespace "polylith.clj.core.validate.m201-mismatching-parameters-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m201_mismatching_parameters_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m201-mismatching-parameters"]}
                                                {:name "m103-missing-defs-test",
                                                 :namespace "polylith.clj.core.validate.m103-missing-defs-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m103_missing_defs_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m103-missing-defs"]}
                                                {:name "m107-missing-componens-in-environment-test",
                                                 :namespace "polylith.clj.core.validate.m107-missing-componens-in-environment-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m107_missing_componens_in_environment_test.clj",
                                                 :imports ["polylith.clj.core.util.interfc.color"
                                                           "polylith.clj.core.validate.m107-missing-componens-in-environment"]}
                                                {:name "m202-missing-libraries-test",
                                                 :namespace "polylith.clj.core.validate.m202-missing-libraries-test",
                                                 :file-path "./components/validate/test/polylith/clj/core/validate/m202_missing_libraries_test.clj",
                                                 :imports ["polylith.clj.core.validate.m202-missing-libraries"]}],
                              :lib-imports-src ["clojure.set" "clojure.string"],
                              :lib-imports-test [],
                              :interface-deps ["common" "deps" "file" "util"],
                              :lib-deps ["clojure"]}
                             {:name "workspace",
                              :type "component",
                              :lines-of-code-src 844,
                              :lines-of-code-test 1008,
                              :interface {:name "workspace",
                                          :definitions [{:name "enrich-workspace",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "user-input"}]}
                                                        {:name "enrich-workspace-str-keys",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "user-input"}]}
                                                        {:name "print-info",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "show-loc?"}]}
                                                        {:name "print-table-str-keys",
                                                         :type "function",
                                                         :parameters [{:name "workspace"} {:name "show-loc?"}]}]},
                              :lib-imports-src ["clojure.set" "clojure.string" "clojure.walk"],
                              :lib-imports-test [],
                              :lib-deps ["clojure"]}
                             {:name "workspace-clj",
                              :type "component",
                              :lines-of-code-src 324,
                              :lines-of-code-test 150,
                              :interface {:name "workspace-clj",
                                          :definitions [{:name "workspace-from-disk", :type "function", :parameters [{:name "ws-dir"}]}
                                                        {:name "workspace-from-disk",
                                                         :type "function",
                                                         :parameters [{:name "ws-dir"} {:name "config"}]}]},
                              :namespaces-src [{:name "interface-defs-from-disk",
                                                :namespace "polylith.clj.core.workspace-clj.interface-defs-from-disk",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/interface_defs_from_disk.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.workspace-clj.definitions"]}
                                               {:name "interfc",
                                                :namespace "polylith.clj.core.workspace-clj.interfc",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/interfc.clj",
                                                :imports ["polylith.clj.core.workspace-clj.core"]}
                                               {:name "environment-from-disk",
                                                :namespace "polylith.clj.core.workspace-clj.environment-from-disk",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/environment_from_disk.clj",
                                                :imports ["clojure.string"
                                                          "clojure.tools.deps.alpha.util.maven"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.workspace-clj.namespaces-from-disk"]}
                                               {:name "core",
                                                :namespace "polylith.clj.core.workspace-clj.core",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/core.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.user-config.interfc"
                                                          "polylith.clj.core.util.interfc"
                                                          "polylith.clj.core.workspace-clj.bases-from-disk"
                                                          "polylith.clj.core.workspace-clj.components-from-disk"
                                                          "polylith.clj.core.workspace-clj.environment-from-disk"
                                                          "polylith.clj.core.workspace-clj.profile"]}
                                               {:name "definitions",
                                                :namespace "polylith.clj.core.workspace-clj.definitions",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/definitions.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.util.interfc"]}
                                               {:name "namespaces-from-disk",
                                                :namespace "polylith.clj.core.workspace-clj.namespaces-from-disk",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/namespaces_from_disk.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.util.interfc.str"]}
                                               {:name "components-from-disk",
                                                :namespace "polylith.clj.core.workspace-clj.components-from-disk",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/components_from_disk.clj",
                                                :imports ["polylith.clj.core.common.interfc"
                                                          "polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.workspace-clj.interface-defs-from-disk"
                                                          "polylith.clj.core.workspace-clj.namespaces-from-disk"]}
                                               {:name "profile",
                                                :namespace "polylith.clj.core.workspace-clj.profile",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/profile.clj",
                                                :imports ["clojure.string" "polylith.clj.core.util.interfc"]}
                                               {:name "bases-from-disk",
                                                :namespace "polylith.clj.core.workspace-clj.bases-from-disk",
                                                :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/bases_from_disk.clj",
                                                :imports ["polylith.clj.core.file.interfc"
                                                          "polylith.clj.core.workspace-clj.namespaces-from-disk"]}],
                              :namespaces-test [{:name "import-from-disk-test",
                                                 :namespace "polylith.clj.core.workspace-clj.import-from-disk-test",
                                                 :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/import_from_disk_test.clj",
                                                 :imports ["polylith.clj.core.workspace-clj.namespaces-from-disk"]}
                                                {:name "readimportsfromdisk-test",
                                                 :namespace "polylith.clj.core.workspace-clj.readimportsfromdisk-test",
                                                 :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/readimportsfromdisk_test.clj",
                                                 :imports ["polylith.clj.core.workspace-clj.namespaces-from-disk"]}
                                                {:name "environment-test",
                                                 :namespace "polylith.clj.core.workspace-clj.environment-test",
                                                 :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/environment_test.clj",
                                                 :imports ["clojure.tools.deps.alpha.util.maven"
                                                           "polylith.clj.core.file.interfc"
                                                           "polylith.clj.core.workspace-clj.environment-from-disk"]}
                                                {:name "definitions-test",
                                                 :namespace "polylith.clj.core.workspace-clj.definitions-test",
                                                 :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/definitions_test.clj",
                                                 :imports ["polylith.clj.core.workspace-clj.definitions"]}],
                              :lib-imports-src ["clojure.string" "clojure.tools.deps.alpha.util.maven"],
                              :lib-imports-test ["clojure.tools.deps.alpha.util.maven"],
                              :interface-deps ["common" "file" "user-config" "util"],
                              :lib-deps ["clojure" "clojure.tools.deps"]}],
                :changes {:sha1 "HEAD",
                          :git-command "git diff HEAD --name-only",
                          :user-input {:run-all? false,
                                       :run-env-tests? false,
                                       :interface nil,
                                       :unnamed-args [],
                                       :active-dev-profiles #{"default"},
                                       :brick nil,
                                       :name nil,
                                       :env nil,
                                       :top-ns nil,
                                       :loc nil,
                                       :selected-environments #{},
                                       :arg1 nil},
                          :changed-components ["entity" "workspace"],
                          :changed-bases [],
                          :changed-environments [],
                          :env->indirect-changes {"cli" ["cli" "command" "test-helper"],
                                                  "core" ["cli" "command" "test-helper"],
                                                  "development" ["cli" "command" "test-helper"]},
                          :env->bricks-to-test {"cli" ["command" "entity" "workspace"], "core" [], "development" []},
                          :environments-to-test [],
                          :changed-files ["components/entity/src/polylith/clj/core/entity/env_statuses.clj"
                                          "components/entity/src/polylith/clj/core/entity/interfc.clj"
                                          "components/entity/src/polylith/clj/core/entity/matchers.clj"
                                          "components/entity/src/polylith/clj/core/entity/status.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/core.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/text_table/new_env_table.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/text_table/ws_table.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/ws_table/core.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/ws_table/env_columns.clj"
                                          "components/workspace/test/polylith/clj/core/workspace/text_table/env_table_test.clj"
                                          "components/workspace/test/polylith/clj/core/workspace/text_table/ws_table_test.clj"
                                          "development/src/dev/jocke.clj"
                                          "todo.txt"]},
                :bases [{:name "cli",
                         :type "base",
                         :lines-of-code-src 22,
                         :lines-of-code-test 0,
                         :namespaces-src [{:name "poly",
                                           :namespace "polylith.clj.core.cli.poly",
                                           :file-path "./bases/cli/src/polylith/clj/core/cli/poly.clj",
                                           :imports ["polylith.clj.core.change.interfc"
                                                     "polylith.clj.core.command.interfc"
                                                     "polylith.clj.core.common.interfc"
                                                     "polylith.clj.core.file.interfc"
                                                     "polylith.clj.core.util.interfc.exception"
                                                     "polylith.clj.core.workspace-clj.interfc"
                                                     "polylith.clj.core.workspace.interfc"]}],
                         :namespaces-test [],
                         :lib-imports-src [],
                         :lib-imports-test [],
                         :interface-deps ["change" "command" "common" "file" "util" "workspace" "workspace-clj"],
                         :lib-deps []}]})




(def workspace-with-profiles (-> workspace
                                 (assoc-in [:settings :active-dev-profiles] #{"default"})
                                 (assoc-in [:settings :profile->settings] {"default" {:src-bricks #{"deps"}
                                                                                      :test-bricks #{"deps"}}
                                                                           "adm" {:src-bricks #{"deps2"}
                                                                                  :test-bricks #{"deps2"}}})))


(deftest ws-table--without-loc-info--return-table-without-loc-info
  (is (= ["  interface      brick          cli  core  dev"
          "  --------------------------------------------"
          "  change         change         xx-  x--   xx-"
          "  command        command        xxx  ---   xx-"
          "  common         common         xx-  x--   xx-"
          "  create         create         xx-  ---   xx-"
          "  deps           deps           xx-  x--   xx-"
          "  entity         entity *       xxx  x--   xx-"
          "  file           file           xx-  x--   xx-"
          "  git            git            xx-  x--   xx-"
          "  help           help           x--  x--   x--"
          "  shell          shell          x--  x--   x--"
          "  test-helper    test-helper    x--  ---   x--"
          "  test-runner    test-runner    x--  ---   x--"
          "  text-table     text-table     ---  ---   ---"
          "  user-config    user-config    x--  x--   x--"
          "  util           util           xx-  x--   xx-"
          "  validate       validate       xx-  x--   xx-"
          "  workspace      workspace *    xxx  x--   xx-"
          "  workspace-clj  workspace-clj  xx-  ---   xx-"
          "  -              cli            x--  ---   x--"]
         (ws-table/table workspace false false))))

(deftest ws-table--with-loc-info--return-table-with-loc-info
  (is (= ["  interface      brick           cli   core    dev     loc   (t)"
          "  --------------------------------------------------------------"
          "  change         change         x-x-   x---   x-x-     134   343"
          "  command        command        x-xx   ----   x-x-     151     0"
          "  common         common         x-x-   x---   x-x-     336    53"
          "  create         create         xxx-   ----   xxx-     181   282"
          "  deps           deps           x-x-   x---   x-x-     242   328"
          "  entity         entity *       x-xx   x---   x-x-     591   343"
          "  file           file           x-x-   x---   x-x-     165     2"
          "  git            git            x-x-   x---   x-x-      55    18"
          "  help           help           x---   x---   x---     204     0"
          "  shell          shell          x---   x---   x---      19     0"
          "  test-helper    test-helper    x---   ----   x---      73     0"
          "  test-runner    test-runner    x---   ----   x---     108     0"
          "  text-table     text-table     ----   ----   ----     145   117"
          "  user-config    user-config    x---   x---   x---      18     0"
          "  util           util           x-x-   x---   x-x-     290    64"
          "  validate       validate       x-x-   x---   x-x-     420   810"
          "  workspace      workspace *    x-xx   x---   x-x-     844 1,008"
          "  workspace-clj  workspace-clj  x-x-   ----   x-x-     324   150"
          "  -              cli            x---   ----   x---      22     0"
          "                                4,177  3,318  4,177  4,322 3,518"]
         (ws-table/table workspace true true))))

(deftest ws-table--with-profiles-without-loc-info--return-table-without-loc-info
  (is (= ["  interface      brick          cli  core  dev  adm"
          "  -------------------------------------------------"
          "  change         change         xx-  x--   xx-  -- "
          "  command        command        xxx  ---   xx-  -- "
          "  common         common         xx-  x--   xx-  -- "
          "  create         create         xx-  ---   xx-  -- "
          "  deps           deps           xx-  x--   xx-  -- "
          "  entity         entity *       xxx  x--   xx-  -- "
          "  file           file           xx-  x--   xx-  -- "
          "  git            git            xx-  x--   xx-  -- "
          "  help           help           x--  x--   x--  -- "
          "  shell          shell          x--  x--   x--  -- "
          "  test-helper    test-helper    x--  ---   x--  -- "
          "  test-runner    test-runner    x--  ---   x--  -- "
          "  text-table     text-table     ---  ---   ---  -- "
          "  user-config    user-config    x--  x--   x--  -- "
          "  util           util           xx-  x--   xx-  -- "
          "  validate       validate       xx-  x--   xx-  -- "
          "  workspace      workspace *    xxx  x--   xx-  -- "
          "  workspace-clj  workspace-clj  xx-  ---   xx-  -- "
          "  -              cli            x--  ---   x--  -- "]
         (ws-table/table workspace-with-profiles false false))))

(deftest ws-table--with-profiles-with-loc-info--return-table-without-loc-info
  (is (= ["  interface      brick           cli   core    dev   adm    loc   (t)"
          "  -------------------------------------------------------------------"
          "  change         change          xx-    x--    xx-   --     134   343"
          "  command        command         xxx    ---    xx-   --     151     0"
          "  common         common          xx-    x--    xx-   --     336    53"
          "  create         create          xx-    ---    xx-   --     181   282"
          "  deps           deps            xx-    x--    xx-   --     242   328"
          "  entity         entity *        xxx    x--    xx-   --     591   343"
          "  file           file            xx-    x--    xx-   --     165     2"
          "  git            git             xx-    x--    xx-   --      55    18"
          "  help           help            x--    x--    x--   --     204     0"
          "  shell          shell           x--    x--    x--   --      19     0"
          "  test-helper    test-helper     x--    ---    x--   --      73     0"
          "  test-runner    test-runner     x--    ---    x--   --     108     0"
          "  text-table     text-table      ---    ---    ---   --     145   117"
          "  user-config    user-config     x--    x--    x--   --      18     0"
          "  util           util            xx-    x--    xx-   --     290    64"
          "  validate       validate        xx-    x--    xx-   --     420   810"
          "  workspace      workspace *     xxx    x--    xx-   --     844 1,008"
          "  workspace-clj  workspace-clj   xx-    ---    xx-   --     324   150"
          "  -              cli             x--    ---    x--   --      22     0"
          "                                4,177  3,318  4,177       4,322 3,518"]
         (ws-table/table workspace-with-profiles true false))))
