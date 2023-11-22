(ns polylith.clj.core.workspace.text-table.ws-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.workspace.text-table.ws-table :as ws-table]))

(def workspace {:user-input {:selected-profiles #{"default"}}
                :interfaces [{:name "workspace-clj",
                              :type "interface",
                              :definitions [{:name "workspace-from-disk", :type "function", :arglist [{:name "ws-dir"}]}
                                            {:name "workspace-from-disk",
                                             :type "function",
                                             :arglist [{:name "ws-dir"} {:name "config"}]}],
                              :implementing-components ["workspace-clj"]}
                             {:name "test-runner",
                              :type "interface",
                              :definitions [{:name "run", :type "function", :arglist [{:name "workspace"}]}],
                              :implementing-components ["test-runner"]}
                             {:name "command",
                              :type "interface",
                              :definitions [{:name "execute-command",
                                             :type "function",
                                             :arglist [{:name "current-dir"} {:name "workspace"} {:name "cmd"} {:name "user-input"}]}],
                              :implementing-components ["command"]}
                             {:name "text-table",
                              :type "interface",
                              :definitions [{:name "line", :type "function", :arglist [{:name "rows"}]}
                                            {:name "line", :type "function", :arglist [{:name "rows"} {:name "visables"}]}
                                            {:name "table",
                                             :type "function",
                                             :arglist [{:name "initial-spaces"}
                                                       {:name "alignments"}
                                                       {:name "colors"}
                                                       {:name "rows"}
                                                       {:name "color-mode"}]}
                                            {:name "table",
                                             :type "function",
                                             :arglist [{:name "initial-spaces"}
                                                       {:name "alignments"}
                                                       {:name "header-colors"}
                                                       {:name "header-orientations"}
                                                       {:name "colors"}
                                                       {:name "headers"}
                                                       {:name "rows"}
                                                       {:name "color-mode"}]}
                                            {:name "table",
                                             :type "function",
                                             :arglist [{:name "initial-spaces"}
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
                                             :arglist [{:name "predicate"} {:name "sequence"}]}
                                            {:name "first-as-vector", :type "function", :arglist [{:name "vals"}]}
                                            {:name "ordered-map", :type "function", :arglist [{:name "&"} {:name "keyvals"}]}
                                            {:name "stringify-and-sort-map", :type "function", :arglist [{:name "m"}]}
                                            {:name "def-keys", :type "macro", :arglist [{:name "amap"} {:name "keys"}]}
                                            {:name "none", :type "data", :sub-ns "color"}
                                            {:name "base",
                                             :type "function",
                                             :arglist [{:name "base"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "blue",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "brick",
                                             :type "function",
                                             :arglist [{:name "type"} {:name "brick"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "clean-colors", :type "function", :arglist [{:name "message"}], :sub-ns "color"}
                                            {:name "colored-text",
                                             :type "function",
                                             :arglist [{:name "color"} {:name "color-mode"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "colored-text",
                                             :type "function",
                                             :arglist [{:name "color-light"}
                                                       {:name "color-dark"}
                                                       {:name "color-mode"}
                                                       {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "component",
                                             :type "function",
                                             :arglist [{:name "component"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "cyan",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "project",
                                             :type "function",
                                             :arglist [{:name "project"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "error",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "green",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "grey",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "interface",
                                             :type "function",
                                             :arglist [{:name "ifc"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "namespc",
                                             :type "function",
                                             :arglist [{:name "namespace"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "namespc",
                                             :type "function",
                                             :arglist [{:name "interface"} {:name "namespace"} {:name "color-mode"}],
                                             :sub-ns "color"}
                                            {:name "ok",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "purple",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "red",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "warning",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "yellow",
                                             :type "function",
                                             :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                             :sub-ns "color"}
                                            {:name "print-error-message",
                                             :type "function",
                                             :arglist [{:name "e"}],
                                             :sub-ns "exception"}
                                            {:name "print-exception", :type "function", :arglist [{:name "e"}], :sub-ns "exception"}
                                            {:name "print-stacktrace",
                                             :type "function",
                                             :arglist [{:name "e"}],
                                             :sub-ns "exception"}
                                            {:name "extract", :type "function", :arglist [{:name "args"}], :sub-ns "params"}
                                            {:name "key-name", :type "function", :arglist [{:name "arg"}], :sub-ns "params"}
                                            {:name "named?", :type "function", :arglist [{:name "arg"}], :sub-ns "params"}
                                            {:name "unnamed?", :type "function", :arglist [{:name "arg"}], :sub-ns "params"}
                                            {:name "count-things",
                                             :type "function",
                                             :arglist [{:name "thing"} {:name "cnt"}],
                                             :sub-ns "str"}
                                            {:name "line", :type "function", :arglist [{:name "length"}], :sub-ns "str"}
                                            {:name "sep-1000",
                                             :type "function",
                                             :arglist [{:name "number"} {:name "sep"}],
                                             :sub-ns "str"}
                                            {:name "skip-if-ends-with",
                                             :type "function",
                                             :arglist [{:name "string"} {:name "ends-with"}],
                                             :sub-ns "str"}
                                            {:name "skip-prefix",
                                             :type "function",
                                             :arglist [{:name "string"} {:name "prefix"}],
                                             :sub-ns "str"}
                                            {:name "skip-suffix",
                                             :type "function",
                                             :arglist [{:name "string"} {:name "suffix"}],
                                             :sub-ns "str"}
                                            {:name "skip-suffixes",
                                             :type "function",
                                             :arglist [{:name "string"} {:name "suffixes"}],
                                             :sub-ns "str"}
                                            {:name "skip-until",
                                             :type "function",
                                             :arglist [{:name "string"} {:name "separator"}],
                                             :sub-ns "str"}
                                            {:name "spaces", :type "function", :arglist [{:name "n#spaces"}], :sub-ns "str"}
                                            {:name "take-until",
                                             :type "function",
                                             :arglist [{:name "string"} {:name "separator"}],
                                             :sub-ns "str"}
                                            {:name "current-time", :type "function", :arglist [], :sub-ns "time"}
                                            {:name "print-execution-time",
                                             :type "function",
                                             :arglist [{:name "start-time"}],
                                             :sub-ns "time"}],
                              :implementing-components ["util"]}
                             {:name "validator",
                              :type "interface",
                              :definitions [{:name "messages",
                                             :type "function",
                                             :arglist [{:name "ws-dir"}
                                                       {:name "suffixed-top-ns"}
                                                       {:name "interface-names"}
                                                       {:name "interfaces"}
                                                       {:name "components"}
                                                       {:name "bases"}
                                                       {:name "projects"}
                                                       {:name "interface-ns"}
                                                       {:name "ns-to-lib"}
                                                       {:name "color-mode"}]}],
                              :implementing-components ["validator"]}
                             {:name "shell",
                              :type "interface",
                              :definitions [{:name "sh", :type "function", :arglist [{:name "&"} {:name "args"}]}],
                              :implementing-components ["shell"]}
                             {:name "workspace",
                              :type "interface",
                              :definitions [{:name "enrich-workspace",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "user-input"}]}
                                            {:name "enrich-workspace-str-keys",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "user-input"}]}
                                            {:name "print-info",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "is-show-loc"}]}
                                            {:name "print-table-str-keys",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "is-show-loc"}]}],
                              :implementing-components ["workspace"]}
                             {:name "user-config",
                              :type "interface",
                              :definitions [{:name "color-mode", :type "function", :arglist []}
                                            {:name "config-content", :type "function", :arglist []}
                                            {:name "home-dir", :type "function", :arglist []}
                                            {:name "thousand-separator", :type "function", :arglist []}],
                              :implementing-components ["user-config"]}
                             {:name "git",
                              :type "interface",
                              :definitions [{:name "add", :type "function", :arglist [{:name "ws-dir"} {:name "filename"}]}
                                            {:name "current-sha", :type "function", :arglist [{:name "ws-dir"}]}
                                            {:name "diff",
                                             :type "function",
                                             :arglist [{:name "ws-dir"} {:name "sha1"} {:name "sha2"}]}
                                            {:name "diff-command", :type "function", :arglist [{:name "sha1"} {:name "sha2"}]}
                                            {:name "init", :type "function", :arglist [{:name "ws-dir"}]}],
                              :implementing-components ["git"]}
                             {:name "deps",
                              :type "interface",
                              :definitions [{:name "interface-deps",
                                             :type "function",
                                             :arglist [{:name "suffixed-top-ns"} {:name "interface-names"} {:name "brick"}]}
                                            {:name "interface-ns-deps",
                                             :type "function",
                                             :arglist [{:name "top-ns"}
                                                       {:name "interface-name"}
                                                       {:name "interface-names"}
                                                       {:name "brick-namespaces"}]}
                                            {:name "print-brick-ifc-table",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "brick-name"} {:name "color-mode"}]}
                                            {:name "print-brick-table",
                                             :type "function",
                                             :arglist [{:name "workspace"}
                                                       {:name "project-name"}
                                                       {:name "brick-name"}
                                                       {:name "color-mode"}]}
                                            {:name "print-workspace-brick-table",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "project-name"} {:name "color-mode"}]}
                                            {:name "print-workspace-ifc-table",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "color-mode"}]}],
                              :implementing-components ["deps"]}
                             {:name "help",
                              :type "interface",
                              :definitions [{:name "print-help", :type "function", :arglist [{:name "cmd"} {:name "color-mode"}]}],
                              :implementing-components ["help"]}
                             {:name "creator",
                              :type "interface",
                              :definitions [{:name "create-base",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "base-name"}]}
                                            {:name "create-component",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "component-name"} {:name "interface-name"}]}
                                            {:name "create-project",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "project"}]}
                                            {:name "create-workspace",
                                             :type "function",
                                             :arglist [{:name "root-dir"} {:name "ws-name"} {:name "top-ns"}]}
                                            {:name "print-alias-message",
                                             :type "function",
                                             :arglist [{:name "project"} {:name "color-mode"}]}],
                              :implementing-components ["creator"]}
                             {:name "file",
                              :type "interface",
                              :definitions [{:name "absolute-path", :type "function", :arglist [{:name "path"}]}
                                            {:name "copy-resource-file!",
                                             :type "function",
                                             :arglist [{:name "source"} {:name "target-path"}]}
                                            {:name "create-dir", :type "function", :arglist [{:name "path", :type "^String"}]}
                                            {:name "create-file", :type "function", :arglist [{:name "path"} {:name "rows"}]}
                                            {:name "create-missing-dirs", :type "function", :arglist [{:name "filename"}]}
                                            {:name "create-temp-dir", :type "function", :arglist [{:name "dir"}]}
                                            {:name "current-dir", :type "function", :arglist []}
                                            {:name "delete-dir", :type "function", :arglist [{:name "path"}]}
                                            {:name "delete-file", :type "function", :arglist [{:name "path"}]}
                                            {:name "delete-folder", :type "function", :arglist [{:name "file"}]}
                                            {:name "directory-paths", :type "function", :arglist [{:name "dir"}]}
                                            {:name "directory?", :type "function", :arglist [{:name "file", :type "^File"}]}
                                            {:name "exists", :type "function", :arglist [{:name "path"}]}
                                            {:name "file-name", :type "function", :arglist [{:name "file", :type "^File"}]}
                                            {:name "files-recursively", :type "function", :arglist [{:name "dir-path"}]}
                                            {:name "lines-of-code", :type "function", :arglist [{:name "file-path"}]}
                                            {:name "paths-recursively", :type "function", :arglist [{:name "dir"}]}
                                            {:name "read-file", :type "function", :arglist [{:name "path"}]}
                                            {:name "relative-paths", :type "function", :arglist [{:name "path"}]}],
                              :implementing-components ["file"]}
                             {:name "path-finder",
                              :type "interface",
                              :definitions [{:name "all-src-deps", :type "function", :arglist [{:name "dep-entries"}]}
                                            {:name "all-test-deps", :type "function", :arglist [{:name "dep-entries"}]}
                                            {:name "brick-status-flags",
                                             :type "function",
                                             :arglist [{:name "path-entries"} {:name "brick-name"}]}
                                            {:name "deps-entries",
                                             :type "function",
                                             :arglist [{:name "is-dev"} {:name "src-deps"} {:name "test-deps"} {:name "settings"}]}
                                            {:name "project-status-flags",
                                             :type "function",
                                             :arglist [{:name "path-entries"} {:name "project-name"}]}
                                            {:name "path-entries",
                                             :type "function",
                                             :arglist [{:name "ws-dir"}
                                                       {:name "is-dev"}
                                                       {:name "src-paths"}
                                                       {:name "test-paths"}
                                                       {:name "settings"}]}
                                            {:name "src-base-names", :type "function", :arglist [{:name "path-entries"}]}
                                            {:name "src-brick-names", :type "function", :arglist [{:name "path-entries"}]}
                                            {:name "src-component-names", :type "function", :arglist [{:name "path-entries"}]}
                                            {:name "src-paths", :type "function", :arglist [{:name "path-entries"}]}
                                            {:name "test-base-names", :type "function", :arglist [{:name "path-entries"}]}
                                            {:name "test-component-names", :type "function", :arglist [{:name "path-entries"}]}
                                            {:name "test-paths", :type "function", :arglist [{:name "path-entries"}]}],
                              :implementing-components ["path-finder"]}
                             {:name "test-helper",
                              :type "interface",
                              :definitions [{:name "content", :type "function", :arglist [{:name "dir"} {:name "filename"}]}
                                            {:name "execute-command",
                                             :type "function",
                                             :arglist [{:name "current-dir"} {:name "cmd"} {:name "&"} {:name "[arg1 arg2 arg3]"}]}
                                            {:name "paths", :type "function", :arglist [{:name "dir"}]}
                                            {:name "root-dir", :type "function", :arglist []}
                                            {:name "test-setup-and-tear-down", :type "function", :arglist [{:name "function"}]}
                                            {:name "user-home", :type "function", :arglist []}],
                              :implementing-components ["test-helper"]}
                             {:name "common",
                              :type "interface",
                              :definitions [{:name "create-class-loader",
                                             :type "function",
                                             :arglist [{:name "paths"} {:name "color-mode"}]}
                                            {:name "eval-in", :type "function", :arglist [{:name "class-loader"} {:name "form"}]}
                                            {:name "filter-clojure-paths", :type "function", :arglist [{:name "paths"}]}
                                            {:name "find-base", :type "function", :arglist [{:name "base-name"} {:name "bases"}]}
                                            {:name "find-brick", :type "function", :arglist [{:name "name"} {:name "workspace"}]}
                                            {:name "find-component",
                                             :type "function",
                                             :arglist [{:name "name"} {:name "components"}]}
                                            {:name "find-project",
                                             :type "function",
                                             :arglist [{:name "project-name"} {:name "projects"}]}
                                            {:name "messages-without-colors", :type "function", :arglist [{:name "workspace"}]}
                                            {:name "ns-to-path", :type "function", :arglist [{:name "namespace"}]}
                                            {:name "path-to-ns", :type "function", :arglist [{:name "namespace"}]}
                                            {:name "pretty-messages", :type "function", :arglist [{:name "workspace"}]}
                                            {:name "pretty-messages",
                                             :type "function",
                                             :arglist [{:name "messages"} {:name "color-mode"}]}
                                            {:name "suffix-ns-with-dot", :type "function", :arglist [{:name "namespace"}]}
                                            {:name "user-input", :type "function", :arglist [{:name "args"}]}
                                            {:name "validate-args",
                                             :type "function",
                                             :arglist [{:name "unnamed-args"} {:name "example"}]}
                                            {:name "base-paths", :type "function", :arglist [{:name "paths"}], :sub-ns "paths"}
                                            {:name "bases-from-paths",
                                             :type "function",
                                             :arglist [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "bricks-from-paths",
                                             :type "function",
                                             :arglist [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "component-paths", :type "function", :arglist [{:name "paths"}], :sub-ns "paths"}
                                            {:name "components-from-paths",
                                             :type "function",
                                             :arglist [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "projects-from-paths",
                                             :type "function",
                                             :arglist [{:name "paths"}],
                                             :sub-ns "paths"}
                                            {:name "src-path?", :type "function", :arglist [{:name "path"}], :sub-ns "paths"}
                                            {:name "test-path?", :type "function", :arglist [{:name "path"}], :sub-ns "paths"}],
                              :implementing-components ["common"]}
                             {:name "change",
                              :type "interface",
                              :definitions [{:name "with-changes",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "user-input"}]}
                                            {:name "with-changes",
                                             :type "function",
                                             :arglist [{:name "workspace"} {:name "changed-files"} {:name "user-input"}]}],
                              :implementing-components ["change"]}],
                :ws-dir ".",
                :name "polylith",
                :settings {:top-namespace "polylith.clj.core",
                           :active-profiles #{"default"}
                           :profile-to-settings {},
                           :ns-to-lib {"clojure" "org.clojure/clojure",
                                       "clojure.core.matrix" "net.mikera/core.matrix",
                                       "clojure.tools.deps" "org.clojure/tools.deps"},
                           :projects {:projects {"poly" {:alias "poly"}
                                                 "core" {:alias "core"}}}
                           :interface-ns "interface",
                           :vcs {:name "git"
                                 :auto-add false}
                           :thousand-separator ",",
                           :color-mode "none"},
                :ws-reader {:name "polylith-clj",
                            :project-url "https://github.com/polyfy/polylith",
                            :reader-version "1.0",
                            :ws-contract-version 1,
                            :language "Clojure",
                            :type-position "postfix",
                            :slash "/",
                            :file-extensions [".clj" "cljc"]},
                :projects [{:name "poly",
                            :alias "poly",
                            :type "project",
                            :is-run-tests true,
                            :is-dev false,
                            :project-dir "./projects/poly",
                            :config-filename "./projects/poly/deps.edn",
                            :lines-of-code {:src 0, :test 5, :total {:src 4558, :test 3520}}
                            :test-component-names ["change"
                                                   "command"
                                                   "common"
                                                   "creator"
                                                   "deps"
                                                   "path-finder"
                                                   "file"
                                                   "git"
                                                   "text-table"
                                                   "util"
                                                   "validator"
                                                   "workspace"
                                                   "workspace-clj"],
                            :component-names ["change"
                                              "command"
                                              "common"
                                              "creator"
                                              "deps"
                                              "path-finder"
                                              "file"
                                              "git"
                                              "help"
                                              "shell"
                                              "test-helper"
                                              "test-runner"
                                              "text-table"
                                              "user-config"
                                              "util"
                                              "validator"
                                              "workspace"
                                              "workspace-clj"],
                            :base-names ["poly-cli"],
                            :test-base-names [],
                            :namespaces {:test [{:name "polylith.clj.core.project.poly-cli",
                                                 :namespace "polylith.clj.core.project.cli",
                                                 :file-path "./projects/poly/test/polylith/clj/core/project/poly.clj",
                                                 :imports []}]}
                            :paths {:src ["bases/poly-cli/src"
                                          "components/change/src"
                                          "components/command/src"
                                          "components/common/src"
                                          "components/creator/resources"
                                          "components/creator/src"
                                          "components/deps/src"
                                          "components/path-finder/src"
                                          "components/file/src"
                                          "components/git/src"
                                          "components/help/src"
                                          "components/shell/src"
                                          "components/test-helper/src"
                                          "components/test-runner/src"
                                          "components/text-table/src"
                                          "components/user-config/src"
                                          "components/util/src"
                                          "components/validator/src"
                                          "components/workspace-clj/src"
                                          "components/workspace/src"]
                                    :test ["bases/poly-cli/test"
                                           "components/change/test"
                                           "components/command/test"
                                           "components/common/test"
                                           "components/creator/test"
                                           "components/deps/test"
                                           "components/path-finder/test"
                                           "components/file/test"
                                           "components/git/test"
                                           "components/help/test"
                                           "components/shell/test"
                                           "components/test-helper/test"
                                           "components/test-runner/test"
                                           "components/text-table/test"
                                           "components/user-config/test"
                                           "components/util/test"
                                           "components/validator/test"
                                           "components/workspace-clj/test"
                                           "components/workspace/test"
                                           "projects/poly/test"]}
                            :lib-imports {:src ["clojure.core.matrix"
                                                "clojure.java.io"
                                                "clojure.java.shell"
                                                "clojure.pprint"
                                                "clojure.set"
                                                "clojure.stacktrace"
                                                "clojure.string"
                                                "clojure.tools.deps"
                                                "clojure.tools.deps.util.maven"
                                                "clojure.walk"]
                                          :test ["clojure.string" "clojure.tools.deps.util.maven"]}
                            :lib-deps {"net.mikera/core.matrix" #:mvn{:version "0.62.0"},
                                       "org.slf4j/slf4j-nop" #:mvn{:version "1.7.25"},
                                       "org.clojure/clojure" #:mvn{:version "1.10.1"},
                                       "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}},
                            :deps {"workspace-clj" {:direct ["common" "file" "user-config" "util"], :indirect []},
                                   "test-runner" {:direct ["common" "util"], :indirect []},
                                   "command" {:direct ["common"
                                                       "creator"
                                                       "deps"
                                                       "help"
                                                       "test-runner"
                                                       "user-config"
                                                       "util"
                                                       "workspace"],
                                              :indirect ["path-finder" "file" "git" "shell" "text-table" "validator"]},
                                   "text-table" {:direct ["util"], :indirect []},
                                   "util" {:direct [], :indirect []},
                                   "validator" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                   "shell" {:direct [], :indirect []},
                                   "workspace" {:direct ["common"
                                                         "deps"
                                                         "path-finder"
                                                         "file"
                                                         "text-table"
                                                         "util"
                                                         "validator"],
                                                :indirect []},
                                   "poly-cli" {:direct ["change" "command" "common" "file" "util" "workspace" "workspace-clj"],
                                               :indirect ["creator"
                                                          "deps"
                                                          "path-finder"
                                                          "git"
                                                          "help"
                                                          "shell"
                                                          "test-runner"
                                                          "text-table"
                                                          "user-config"
                                                          "validator"]},
                                   "user-config" {:direct ["util"], :indirect []},
                                   "git" {:direct ["shell"], :indirect []},
                                   "deps" {:direct ["common" "text-table" "util"], :indirect []},
                                   "help" {:direct ["util"], :indirect []},
                                   "creator" {:direct ["common" "file" "git" "user-config" "util"], :indirect ["shell"]},
                                   "file" {:direct ["util"], :indirect []},
                                   "path-finder" {:direct ["file" "util"], :indirect []},
                                   "test-helper" {:direct ["change"
                                                           "command"
                                                           "common"
                                                           "file"
                                                           "git"
                                                           "user-config"
                                                           "workspace"
                                                           "workspace-clj"],
                                                  :indirect ["creator"
                                                             "deps"
                                                             "path-finder"
                                                             "help"
                                                             "shell"
                                                             "test-runner"
                                                             "text-table"
                                                             "util"
                                                             "validator"]},
                                   "common" {:direct ["util"], :indirect []},
                                   "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},
                            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                          "clojars" {:url "https://repo.clojars.org/"}}}
                           {:name "core",
                            :alias "core",
                            :type "project",
                            :is-run-tests true,
                            :is-dev false,
                            :project-dir "./projects/core",
                            :config-filename "./projects/core/deps.edn",
                            :lines-of-code {:src 0, :test 6, :total {:src 3699, :test 3088}}
                            :test-component-names [],
                            :component-names ["change"
                                              "common"
                                              "deps"
                                              "path-finder"
                                              "file"
                                              "git"
                                              "help"
                                              "shell"
                                              "text-table"
                                              "user-config"
                                              "util"
                                              "validator"
                                              "workspace"],
                            :base-names [],
                            :test-base-names [],
                            :namespaces {:test [{:name "polylith.clj.core.dev-test",
                                                 :namespace "polylith.clj.core.dev-test",
                                                 :file-path "./projects/core/test/polylith/clj/core/dev_test.clj",
                                                 :imports []}]},
                            :paths {:src ["components/change/src"
                                          "components/common/src"
                                          "components/deps/src"
                                          "components/path-finder/src"
                                          "components/file/src"
                                          "components/git/src"
                                          "components/help/src"
                                          "components/shell/src"
                                          "components/text-table/src"
                                          "components/user-config/src"
                                          "components/util/src"
                                          "components/validator/src"
                                          "components/workspace/src"],}
                            :lib-imports {:src ["clojure.core.matrix"
                                                "clojure.java.io"
                                                "clojure.java.shell"
                                                "clojure.set"
                                                "clojure.stacktrace"
                                                "clojure.string"
                                                "clojure.walk"]
                                          :test ["clojure.string"]}
                            :lib-deps {"net.mikera/core.matrix" #:mvn{:version "0.62.0"},
                                       "org.clojure/clojure" #:mvn{:version "1.10.1"},
                                       "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}},
                            :deps {"workspace-clj" {:direct ["common" "file" "user-config" "util"], :indirect []},
                                   "test-runner" {:direct ["common" "util"], :indirect []},
                                   "command" {:direct ["common" "deps" "help" "user-config" "util" "workspace"],
                                              :indirect ["path-finder" "file" "text-table" "text-table2" "validator"]},
                                   "text-table" {:direct ["util"], :indirect []},
                                   "util" {:direct [], :indirect []},
                                   "validator" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                   "text-table2" {:direct ["util"], :indirect []},
                                   "shell" {:direct [], :indirect []},
                                   "workspace" {:direct ["common"
                                                         "deps"
                                                         "path-finder"
                                                         "file"
                                                         "text-table"
                                                         "util"
                                                         "validator"],
                                                :indirect []},
                                   "poly-cli" {:direct ["change" "common" "file" "util" "workspace"],
                                               :indirect ["deps" "path-finder" "git" "shell" "text-table" "text-table2" "validator"]},
                                   "user-config" {:direct ["util"], :indirect []},
                                   "git" {:direct ["shell"], :indirect []},
                                   "deps" {:direct ["common" "text-table" "util"], :indirect []},
                                   "help" {:direct ["util"], :indirect []},
                                   "creator" {:direct ["common" "file" "git" "user-config" "util"], :indirect ["shell"]},
                                   "file" {:direct ["util"], :indirect []},
                                   "path-finder" {:direct ["file" "util"], :indirect []},
                                   "test-helper" {:direct ["change" "common" "file" "git" "user-config" "workspace"],
                                                  :indirect ["deps" "path-finder" "shell" "text-table" "text-table2" "util" "validator"]},
                                   "common" {:direct ["util"], :indirect []},
                                   "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},
                            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                          "clojars" {:url "https://repo.clojars.org/"}}}
                           {:name "development",
                            :alias "dev",
                            :type "project",
                            :is-run-tests false,
                            :is-dev true,
                            :project-dir "./development",
                            :config-filename "./deps.edn",
                            :lines-of-code {:src 100, :test 0, :total {:src 4558, :test 3520}}
                            :test-component-names ["change"
                                                   "command"
                                                   "common"
                                                   "creator"
                                                   "deps"
                                                   "path-finder"
                                                   "git"
                                                   "text-table"
                                                   "util"
                                                   "validator"
                                                   "workspace"
                                                   "workspace-clj"],
                            :component-names ["change"
                                              "command"
                                              "common"
                                              "creator"
                                              "deps"
                                              "path-finder"
                                              "git"
                                              "help"
                                              "shell"
                                              "test-helper"
                                              "test-runner"
                                              "text-table"
                                              "user-config"
                                              "util"
                                              "validator"
                                              "workspace"
                                              "workspace-clj"],
                            :base-names ["poly-cli"],
                            :test-base-names [],
                            :namespaces {:src [{:name "dev.jocke",
                                                :namespace "dev.jocke",
                                                :file-path "./development/src/dev/jocke.clj",
                                                :imports ["clojure.string"
                                                          "polylith.clj.core.change.interface"
                                                          "polylith.clj.core.common.interface"
                                                          "polylith.clj.core.path-finder.interface"
                                                          "polylith.clj.core.file.interface"
                                                          "polylith.clj.core.help.interface"
                                                          "polylith.clj.core.util.interface"
                                                          "polylith.clj.core.workspace-clj.interface"
                                                          "polylith.clj.core.workspace.interface"]}],}
                            :paths {:src ["bases/poly-cli/src"
                                          "components/change/src"
                                          "components/command/src"
                                          "components/common/src"
                                          "components/create/resources"
                                          "components/create/src"
                                          "components/deps/src"
                                          "components/path-finder/src"
                                          "components/git/src"
                                          "components/help/src"
                                          "components/shell/src"
                                          "components/test-helper/src"
                                          "components/test-runner/src"
                                          "components/text-table/src"
                                          "components/user-config/src"
                                          "components/util/src"
                                          "components/validator/src"
                                          "components/workspace-clj/src"
                                          "components/workspace/src"
                                          "development/src"]
                                    :test ["bases/poly-cli/test"
                                           "components/change/test"
                                           "components/command/test"
                                           "components/common/test"
                                           "components/create/test"
                                           "components/deps/test"
                                           "components/path-finder/test"
                                           "components/git/test"
                                           "components/help/test"
                                           "components/shell/test"
                                           "components/test-helper/test"
                                           "components/test-runner/test"
                                           "components/text-table/test"
                                           "components/user-config/test"
                                           "components/util/test"
                                           "components/validator/test"
                                           "components/workspace-clj/test"
                                           "components/workspace/test"
                                           "projects/poly/test"]}
                            :lib-imports {:src ["clojure.core.matrix"
                                                "clojure.java.io"
                                                "clojure.java.shell"
                                                "clojure.pprint"
                                                "clojure.set"
                                                "clojure.stacktrace"
                                                "clojure.string"
                                                "clojure.tools.deps"
                                                "clojure.tools.deps.util.maven"
                                                "clojure.walk"]
                                          :test ["clojure.string" "clojure.tools.deps.util.maven"]}
                            :lib-deps {"net.mikera/core.matrix" #:mvn{:version "0.62.0"},
                                       "org.slf4j/slf4j-nop" #:mvn{:version "1.7.25"},
                                       "org.clojure/clojure" #:mvn{:version "1.10.1"},
                                       "org.clojure/tools.deps"#:mvn{:version "0.16.1264"}},
                            :deps {"workspace-clj" {:direct ["common" "file" "user-config" "util"], :indirect []},
                                   "test-runner" {:direct ["common" "util"], :indirect []},
                                   "command" {:direct ["common"
                                                       "creator"
                                                       "deps"
                                                       "help"
                                                       "test-runner"
                                                       "user-config"
                                                       "util"
                                                       "workspace"],
                                              :indirect ["path-finder" "file" "git" "shell" "text-table" "text-table2" "validator"]},
                                   "text-table" {:direct ["util"], :indirect []},
                                   "util" {:direct [], :indirect []},
                                   "validator" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                   "text-table2" {:direct ["util"], :indirect []},
                                   "shell" {:direct [], :indirect []},
                                   "workspace" {:direct ["common"
                                                         "deps"
                                                         "path-finder"
                                                         "file"
                                                         "text-table"
                                                         "util"
                                                         "validator"],
                                                :indirect []},
                                   "poly-cli" {:direct ["change" "command" "common" "file" "util" "workspace" "workspace-clj"],
                                               :indirect ["creator"
                                                          "deps"
                                                          "path-finder"
                                                          "git"
                                                          "help"
                                                          "shell"
                                                          "test-runner"
                                                          "text-table"
                                                          "user-config"
                                                          "validator"]},
                                   "user-config" {:direct ["util"], :indirect []},
                                   "git" {:direct ["shell"], :indirect []},
                                   "deps" {:direct ["common" "text-table" "util"], :indirect []},
                                   "help" {:direct ["util"], :indirect []},
                                   "creator" {:direct ["common" "file" "git" "user-config" "util"], :indirect ["shell"]},
                                   "file" {:direct ["util"], :indirect []},
                                   "path-finder" {:direct ["file" "util"], :indirect []},
                                   "test-helper" {:direct ["change"
                                                           "command"
                                                           "common"
                                                           "file"
                                                           "git"
                                                           "user-config"
                                                           "workspace"
                                                           "workspace-clj"],
                                                  :indirect ["creator"
                                                             "deps"
                                                             "path-finder"
                                                             "help"
                                                             "shell"
                                                             "test-runner"
                                                             "text-table"
                                                             "util"
                                                             "validator"]},
                                   "common" {:direct ["util"], :indirect []},
                                   "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},
                            :maven-repos {"central" {:url "https://repo1.maven.org/maven2/"},
                                          "clojars" {:url "https://repo.clojars.org/"}}}],
                :messages [],
                :components [{:name "change",
                              :type "component",
                              :lines-of-code {:src 134, :test 343}
                              :interface {:name "change",
                                          :definitions [{:name "with-changes",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "user-input"}]}
                                                        {:name "with-changes",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "changed-files"} {:name "user-input"}]}]},
                              :namespaces {:src [{:name "to-test",
                                                  :namespace "polylith.clj.core.change.to-test",
                                                  :file-path "./components/change/src/polylith/clj/core/change/to_test.clj",
                                                  :imports ["clojure.set" "polylith.clj.core.change.project"]}
                                                 {:name "interface",
                                                  :namespace "polylith.clj.core.change.interface",
                                                  :file-path "./components/change/src/polylith/clj/core/change/interface.clj",
                                                  :imports ["polylith.clj.core.change.core"]}
                                                 {:name "project",
                                                  :namespace "polylith.clj.core.change.project",
                                                  :file-path "./components/change/src/polylith/clj/core/change/project.clj",
                                                  :imports ["clojure.set"]}
                                                 {:name "core",
                                                  :namespace "polylith.clj.core.change.core",
                                                  :file-path "./components/change/src/polylith/clj/core/change/core.clj",
                                                  :imports ["polylith.clj.core.change.path-finder"
                                                            "polylith.clj.core.change.indirect"
                                                            "polylith.clj.core.change.to-test"
                                                            "polylith.clj.core.git.interface"
                                                            "polylith.clj.core.util.interface"
                                                            "polylith.clj.core.util.interface"]}
                                                 {:name "path-finder",
                                                  :namespace "polylith.clj.core.change.path-finder",
                                                  :file-path "./components/change/src/polylith/clj/core/change/path-finder.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.common.interface.paths"
                                                            "polylith.clj.core.git.interface"]}
                                                 {:name "indirect",
                                                  :namespace "polylith.clj.core.change.indirect",
                                                  :file-path "./components/change/src/polylith/clj/core/change/indirect.clj",
                                                  :imports ["clojure.set"]}]
                                           :test [{:name "core-test",
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
                                                   :imports ["polylith.clj.core.change.path-finder"]}
                                                  {:name "to-test-test",
                                                   :namespace "polylith.clj.core.change.to-test-test",
                                                   :file-path "./components/change/test/polylith/clj/core/change/to_test_test.clj",
                                                   :imports ["polylith.clj.core.change.to-test"]}
                                                  {:name "project-test",
                                                   :namespace "polylith.clj.core.change.project-test",
                                                   :file-path "./components/change/test/polylith/clj/core/change/project_test.clj",
                                                   :imports ["polylith.clj.core.change.project"]}]}
                              :lib-imports {:src ["clojure.set" "clojure.string"],}
                              :interface-deps {:src ["common" "git" "util"],}
                              :lib-deps ["clojure"]}
                             {:name "command",
                              :type "component",
                              :lines-of-code {:src  151, :test 0}
                              :interface {:name "command",
                                          :definitions [{:name "execute-command",
                                                         :type "function",
                                                         :arglist [{:name "current-dir"}
                                                                   {:name "workspace"}
                                                                   {:name "cmd"}
                                                                   {:name "user-input"}]}]},
                              :namespaces {:src [{:name "creator",
                                                  :namespace "polylith.clj.core.command.create",
                                                  :file-path "./components/command/src/polylith/clj/core/command/create.clj",
                                                  :imports ["polylith.clj.core.command.message" "polylith.clj.core.create.interface"]}
                                                 {:name "deps",
                                                  :namespace "polylith.clj.core.command.deps",
                                                  :file-path "./components/command/src/polylith/clj/core/command/deps.clj",
                                                  :imports ["polylith.clj.core.common.interface" "polylith.clj.core.deps.interface"]}
                                                 {:name "interface",
                                                  :namespace "polylith.clj.core.command.interface",
                                                  :file-path "./components/command/src/polylith/clj/core/command/interface.clj",
                                                  :imports ["polylith.clj.core.command.core"]}
                                                 {:name "exit-code",
                                                  :namespace "polylith.clj.core.command.exit-code",
                                                  :file-path "./components/command/src/polylith/clj/core/command/exit_code.clj",
                                                  :imports []}
                                                 {:name "info",
                                                  :namespace "polylith.clj.core.command.info",
                                                  :file-path "./components/command/src/polylith/clj/core/command/info.clj",
                                                  :imports ["polylith.clj.core.common.interface" "polylith.clj.core.workspace.interface"]}
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
                                                            "polylith.clj.core.common.interface"
                                                            "polylith.clj.core.help.interface"
                                                            "polylith.clj.core.user-config.interface"
                                                            "polylith.clj.core.util.interface.color"
                                                            "polylith.clj.core.util.interface.params"]}
                                                 {:name "message",
                                                  :namespace "polylith.clj.core.command.message",
                                                  :file-path "./components/command/src/polylith/clj/core/command/message.clj",
                                                  :imports []}
                                                 {:name "test",
                                                  :namespace "polylith.clj.core.command.test",
                                                  :file-path "./components/command/src/polylith/clj/core/command/test.clj",
                                                  :imports ["polylith.clj.core.common.interface" "polylith.clj.core.test-runner.interface"]}],}
                              :lib-imports {:src ["clojure.pprint"]}
                              :interface-deps {:src ["common" "creator" "deps" "help" "test-runner" "user-config" "util" "workspace"],}
                              :lib-deps ["clojure"]}
                             {:name "common",
                              :type "component",
                              :lines-of-code {:src 336, :test 53}
                              :interface {:name "common",
                                          :definitions [{:name "create-class-loader",
                                                         :type "function",
                                                         :arglist [{:name "paths"} {:name "color-mode"}]}
                                                        {:name "eval-in",
                                                         :type "function",
                                                         :arglist [{:name "class-loader"} {:name "form"}]}
                                                        {:name "filter-clojure-paths", :type "function", :arglist [{:name "paths"}]}
                                                        {:name "find-base",
                                                         :type "function",
                                                         :arglist [{:name "base-name"} {:name "bases"}]}
                                                        {:name "find-brick",
                                                         :type "function",
                                                         :arglist [{:name "name"} {:name "workspace"}]}
                                                        {:name "find-component",
                                                         :type "function",
                                                         :arglist [{:name "name"} {:name "components"}]}
                                                        {:name "find-project",
                                                         :type "function",
                                                         :arglist [{:name "project-name"} {:name "projects"}]}
                                                        {:name "messages-without-colors",
                                                         :type "function",
                                                         :arglist [{:name "workspace"}]}
                                                        {:name "ns-to-path", :type "function", :arglist [{:name "namespace"}]}
                                                        {:name "path-to-ns", :type "function", :arglist [{:name "namespace"}]}
                                                        {:name "pretty-messages", :type "function", :arglist [{:name "workspace"}]}
                                                        {:name "pretty-messages",
                                                         :type "function",
                                                         :arglist [{:name "messages"} {:name "color-mode"}]}
                                                        {:name "suffix-ns-with-dot",
                                                         :type "function",
                                                         :arglist [{:name "namespace"}]}
                                                        {:name "user-input", :type "function", :arglist [{:name "args"}]}
                                                        {:name "validate-args",
                                                         :type "function",
                                                         :arglist [{:name "unnamed-args"} {:name "example"}]}
                                                        {:name "base-paths",
                                                         :type "function",
                                                         :arglist [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "bases-from-paths",
                                                         :type "function",
                                                         :arglist [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "bricks-from-paths",
                                                         :type "function",
                                                         :arglist [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "component-paths",
                                                         :type "function",
                                                         :arglist [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "components-from-paths",
                                                         :type "function",
                                                         :arglist [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "projects-from-paths",
                                                         :type "function",
                                                         :arglist [{:name "paths"}],
                                                         :sub-ns "paths"}
                                                        {:name "src-path?",
                                                         :type "function",
                                                         :arglist [{:name "path"}],
                                                         :sub-ns "paths"}
                                                        {:name "test-path?",
                                                         :type "function",
                                                         :arglist [{:name "path"}],
                                                         :sub-ns "paths"}]},
                              :namespaces {:src [{:name "user-input",
                                                  :namespace "polylith.clj.core.common.user-input",
                                                  :file-path "./components/common/src/polylith/clj/core/common/user_input.clj",
                                                  :imports ["clojure.string" "polylith.clj.core.util.interface.params"]}
                                                 {:name "interface",
                                                  :namespace "polylith.clj.core.common.interface",
                                                  :file-path "./components/common/src/polylith/clj/core/common/interface.clj",
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
                                                  :imports ["clojure.string" "polylith.clj.core.util.interface"]}
                                                 {:name "message",
                                                  :namespace "polylith.clj.core.common.message",
                                                  :file-path "./components/common/src/polylith/clj/core/common/message.clj",
                                                  :imports ["clojure.string" "polylith.clj.core.util.interface.color"]}
                                                 {:name "class-loader",
                                                  :namespace "polylith.clj.core.common.class-loader",
                                                  :file-path "./components/common/src/polylith/clj/core/common/class_loader.clj",
                                                  :imports ["clojure.java.io" "clojure.string" "polylith.clj.core.util.interface.color"]}
                                                 {:name "interface.paths",
                                                  :namespace "polylith.clj.core.common.interface.paths",
                                                  :file-path "./components/common/src/polylith/clj/core/common/interface/paths.clj",
                                                  :imports ["polylith.clj.core.common.paths"]}
                                                 {:name "validate-args",
                                                  :namespace "polylith.clj.core.common.validate-args",
                                                  :file-path "./components/common/src/polylith/clj/core/common/validate_args.clj",
                                                  :imports ["clojure.string"]}]
                                           :test [{:name "user-input-test",
                                                   :namespace "polylith.clj.core.common.user-input-test",
                                                   :file-path "./components/common/test/polylith/clj/core/common/user_input_test.clj",
                                                   :imports ["polylith.clj.core.common.user-input"]}]}
                              :lib-imports {:src ["clojure.java.io" "clojure.string"],}
                              :interface-deps {:src ["util"],}
                              :lib-deps ["clojure"]}
                             {:name "creator",
                              :type "component",
                              :lines-of-code {:src 181, :test 282}
                              :interface {:name "creator",
                                          :definitions [{:name "create-base",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "base-name"}]}
                                                        {:name "create-component",
                                                         :type "function",
                                                         :arglist [{:name "workspace"}
                                                                   {:name "component-name"}
                                                                   {:name "interface-name"}]}
                                                        {:name "create-project",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "project"}]}
                                                        {:name "create-workspace",
                                                         :type "function",
                                                         :arglist [{:name "root-dir"} {:name "ws-name"} {:name "top-ns"}]}
                                                        {:name "print-alias-message",
                                                         :type "function",
                                                         :arglist [{:name "project"} {:name "color-mode"}]}]},
                              :namespaces {:src [{:name "interface",
                                                  :namespace "polylith.clj.core.create.interface",
                                                  :file-path "./components/create/src/polylith/clj/core/create/interface.clj",
                                                  :imports ["polylith.clj.core.create.base"
                                                            "polylith.clj.core.create.component"
                                                            "polylith.clj.core.create.project"
                                                            "polylith.clj.core.create.workspace"]}
                                                 {:name "brick",
                                                  :namespace "polylith.clj.core.create.brick",
                                                  :file-path "./components/create/src/polylith/clj/core/create/brick.clj",
                                                  :imports ["polylith.clj.core.common.interface"
                                                            "polylith.clj.core.file.interface"
                                                            "polylith.clj.core.git.interface"]}
                                                 {:name "project",
                                                  :namespace "polylith.clj.core.create.project",
                                                  :file-path "./components/create/src/polylith/clj/core/create/project.clj",
                                                  :imports ["polylith.clj.core.common.interface"
                                                            "polylith.clj.core.file.interface"
                                                            "polylith.clj.core.git.interface"
                                                            "polylith.clj.core.util.interface.color"]}
                                                 {:name "base",
                                                  :namespace "polylith.clj.core.create.base",
                                                  :file-path "./components/create/src/polylith/clj/core/create/base.clj",
                                                  :imports ["polylith.clj.core.create.brick"]}
                                                 {:name "workspace",
                                                  :namespace "polylith.clj.core.create.workspace",
                                                  :file-path "./components/create/src/polylith/clj/core/create/workspace.clj",
                                                  :imports ["polylith.clj.core.file.interface"
                                                            "polylith.clj.core.git.interface"
                                                            "polylith.clj.core.user-config.interface"]}
                                                 {:name "component",
                                                  :namespace "polylith.clj.core.create.component",
                                                  :file-path "./components/create/src/polylith/clj/core/create/component.clj",
                                                  :imports ["polylith.clj.core.create.brick"]}]
                                           :test [{:name "base-test",
                                                   :namespace "polylith.clj.core.create.base-test",
                                                   :file-path "./components/create/test/polylith/clj/core/create/base_test.clj",
                                                   :imports ["polylith.clj.core.create.brick" "polylith.clj.core.test-helper.interface"]}
                                                  {:name "workspace-test",
                                                   :namespace "polylith.clj.core.create.workspace-test",
                                                   :file-path "./components/create/test/polylith/clj/core/create/workspace_test.clj",
                                                   :imports ["polylith.clj.core.test-helper.interface"]}
                                                  {:name "project-test",
                                                   :namespace "polylith.clj.core.create.project-test",
                                                   :file-path "./components/create/test/polylith/clj/core/create/project_test.clj",
                                                   :imports ["polylith.clj.core.test-helper.interface"
                                                             "polylith.clj.core.util.interface.color"]}
                                                  {:name "component-test",
                                                   :namespace "polylith.clj.core.create.component-test",
                                                   :file-path "./components/create/test/polylith/clj/core/create/component_test.clj",
                                                   :imports ["polylith.clj.core.create.brick" "polylith.clj.core.test-helper.interface"]}]}
                              :lib-imports {}
                              :interface-deps {:src ["common" "file" "git" "user-config" "util"],}
                              :lib-deps []}
                             {:name "deps",
                              :type "component",
                              :lines-of-code {:src 242, :test 328}
                              :interface {:name "deps",
                                          :definitions [{:name "interface-deps",
                                                         :type "function",
                                                         :arglist [{:name "suffixed-top-ns"}
                                                                   {:name "interface-names"}
                                                                   {:name "brick"}]}
                                                        {:name "interface-ns-deps",
                                                         :type "function",
                                                         :arglist [{:name "top-ns"}
                                                                   {:name "interface-name"}
                                                                   {:name "interface-names"}
                                                                   {:name "brick-namespaces"}]}
                                                        {:name "print-brick-ifc-table",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "brick-name"} {:name "color-mode"}]}
                                                        {:name "print-brick-table",
                                                         :type "function",
                                                         :arglist [{:name "workspace"}
                                                                   {:name "project-name"}
                                                                   {:name "brick-name"}
                                                                   {:name "color-mode"}]}
                                                        {:name "print-workspace-brick-table",
                                                         :type "function",
                                                         :arglist [{:name "workspace"}
                                                                   {:name "project-name"}
                                                                   {:name "color-mode"}]}
                                                        {:name "print-workspace-ifc-table",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "color-mode"}]}]},
                              :namespaces {:src [{:name "interface",
                                                  :namespace "polylith.clj.core.deps.interface",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/interface.clj",
                                                  :imports ["polylith.clj.core.deps.interface-deps"
                                                            "polylith.clj.core.deps.text-table.brick-deps-table"
                                                            "polylith.clj.core.deps.text-table.brick-ifc-deps-table"
                                                            "polylith.clj.core.deps.text-table.workspace-brick-deps-table"
                                                            "polylith.clj.core.deps.text-table.workspace-ifc-deps-table"]}
                                                 {:name "text-table.workspace-ifc-deps-table",
                                                  :namespace "polylith.clj.core.deps.text-table.workspace-ifc-deps-table",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/text_table/workspace_ifc_deps_table.clj",
                                                  :imports ["polylith.clj.core.text-table.interface"]}
                                                 {:name "text-table.brick-deps-table",
                                                  :namespace "polylith.clj.core.deps.text-table.brick-deps-table",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/text_table/brick_deps_table.clj",
                                                  :imports ["polylith.clj.core.common.interface"
                                                            "polylith.clj.core.deps.brick-deps"
                                                            "polylith.clj.core.text-table.interface"
                                                            "polylith.clj.core.util.interface.color"]}
                                                 {:name "text-table.brick-ifc-deps-table",
                                                  :namespace "polylith.clj.core.deps.text-table.brick-ifc-deps-table",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/text_table/brick_ifc_deps_table.clj",
                                                  :imports ["polylith.clj.core.common.interface" "polylith.clj.core.text-table.interface"]}
                                                 {:name "text-table.workspace-brick-deps-table",
                                                  :namespace "polylith.clj.core.deps.text-table.workspace-brick-deps-table",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/text_table/workspace_brick_deps_table.clj",
                                                  :imports ["polylith.clj.core.common.interface"
                                                            "polylith.clj.core.text-table.interface"
                                                            "polylith.clj.core.util.interface.color"]}
                                                 {:name "interface-deps",
                                                  :namespace "polylith.clj.core.deps.interface-deps",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/interface_deps.clj",
                                                  :imports ["clojure.string"]}
                                                 {:name "brick-deps",
                                                  :namespace "polylith.clj.core.deps.brick-deps",
                                                  :file-path "./components/deps/src/polylith/clj/core/deps/brick_deps.clj",
                                                  :imports ["clojure.set"]}]
                                           :test [{:name "brick-deps-test",
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
                                                             "polylith.clj.core.util.interface.color"]}
                                                  {:name "text-table.workspace-brick-deps-table-test",
                                                   :namespace "polylith.clj.core.deps.text-table.workspace-brick-deps-table-test",
                                                   :file-path "./components/deps/test/polylith/clj/core/deps/text_table/workspace_brick_deps_table_test.clj",
                                                   :imports ["clojure.string"
                                                             "polylith.clj.core.deps.text-table.workspace-brick-deps-table"
                                                             "polylith.clj.core.util.interface.color"]}
                                                  {:name "text-table.workspace-ifc-deps-table-test",
                                                   :namespace "polylith.clj.core.deps.text-table.workspace-ifc-deps-table-test",
                                                   :file-path "./components/deps/test/polylith/clj/core/deps/text_table/workspace_ifc_deps_table_test.clj",
                                                   :imports ["clojure.string"
                                                             "polylith.clj.core.deps.text-table.workspace-ifc-deps-table"
                                                             "polylith.clj.core.util.interface.color"]}
                                                  {:name "text-table.brick-deps-table-test",
                                                   :namespace "polylith.clj.core.deps.text-table.brick-deps-table-test",
                                                   :file-path "./components/deps/test/polylith/clj/core/deps/text_table/brick_deps_table_test.clj",
                                                   :imports ["clojure.string"
                                                             "polylith.clj.core.deps.text-table.brick-deps-table"
                                                             "polylith.clj.core.util.interface.color"]}]}
                              :lib-imports {:src ["clojure.set" "clojure.string"]
                                            :test ["clojure.string"]}
                              :interface-deps {:src ["common" "text-table" "util"],}
                              :lib-deps ["clojure"]}
                             {:name "path-finder",
                              :type "component",
                              :lines-of-code {:src 591, :test 343}
                              :interface {:name "path-finder",
                                          :definitions [{:name "all-src-deps", :type "function", :arglist [{:name "dep-entries"}]}
                                                        {:name "all-test-deps", :type "function", :arglist [{:name "dep-entries"}]}
                                                        {:name "brick-status-flags",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"} {:name "brick-name"}]}
                                                        {:name "deps-entries",
                                                         :type "function",
                                                         :arglist [{:name "is-dev"}
                                                                   {:name "src-deps"}
                                                                   {:name "test-deps"}
                                                                   {:name "settings"}]}
                                                        {:name "project-status-flags",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"} {:name "project-name"}]}
                                                        {:name "path-entries",
                                                         :type "function",
                                                         :arglist [{:name "ws-dir"}
                                                                   {:name "is-dev"}
                                                                   {:name "src-paths"}
                                                                   {:name "test-paths"}
                                                                   {:name "settings"}]}
                                                        {:name "src-base-names",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"}]}
                                                        {:name "src-brick-names",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"}]}
                                                        {:name "src-component-names",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"}]}
                                                        {:name "src-paths", :type "function", :arglist [{:name "path-entries"}]}
                                                        {:name "test-base-names",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"}]}
                                                        {:name "test-component-names",
                                                         :type "function",
                                                         :arglist [{:name "path-entries"}]}
                                                        {:name "test-paths", :type "function", :arglist [{:name "path-entries"}]}]},
                              :namespaces {:src [{:name "profile-extractor",
                                                  :namespace "polylith.clj.core.path-finder.profile-extractor",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/profile_extractor.clj",
                                                  :imports ["clojure.string"]}
                                                 {:name "path-extractor",
                                                  :namespace "polylith.clj.core.path-finder.path-extractor",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/path_extractor.clj",
                                                  :imports ["polylith.clj.core.file.interfc"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.str"]}
                                                 {:name "dep-selector",
                                                  :namespace "polylith.clj.core.path-finder.dep-selector",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/dep_selector.clj",
                                                  :imports ["polylith.clj.core.path-finder.matchers"]}
                                                 {:name "interfc",
                                                  :namespace "polylith.clj.core.path-finder.interfc",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/interfc.clj",
                                                  :imports ["polylith.clj.core.path-finder.core"
                                                            "polylith.clj.core.path-finder.dep-selector"
                                                            "polylith.clj.core.path-finder.path-selector"
                                                            "polylith.clj.core.path-finder.status"]}
                                                 {:name "matchers",
                                                  :namespace "polylith.clj.core.path-finder.matchers",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/matchers.clj",
                                                  :imports ["clojure.string"]}
                                                 {:name "core",
                                                  :namespace "polylith.clj.core.path-finder.core",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/core.clj",
                                                  :imports ["polylith.clj.core.path-finder.dep-extractor"
                                                            "polylith.clj.core.path-finder.path-extractor"
                                                            "polylith.clj.core.path-finder.profile-extractor"]}
                                                 {:name "project-statuses",
                                                  :namespace "polylith.clj.core.path-finder.project-statuses",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/project_statuses.clj",
                                                  :imports ["polylith.clj.core.path-finder.matchers"]}
                                                 {:name "dep-extractor",
                                                  :namespace "polylith.clj.core.path-finder.dep-extractor",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/dep_extractor.clj",
                                                  :imports ["polylith.clj.core.util.interfc"]}
                                                 {:name "path-selector",
                                                  :namespace "polylith.clj.core.path-finder.path-selector",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/path_selector.clj",
                                                  :imports ["polylith.clj.core.path-finder.matchers"]}
                                                 {:name "status",
                                                  :namespace "polylith.clj.core.path-finder.status",
                                                  :file-path "./components/path-finder/src/polylith/clj/core/path-finder/status.clj",
                                                  :imports ["polylith.clj.core.path-finder.matchers"]}]
                                           :test [{:name "dep-selector-test",
                                                   :namespace "polylith.clj.core.path-finder.dep-selector-test",
                                                   :file-path "./components/path-finder/test/polylith/clj/core/path-finder/dep_selector_test.clj",
                                                   :imports ["polylith.clj.core.path-finder.dep-selector"
                                                             "polylith.clj.core.path-finder.test-data"]}
                                                  {:name "path-selector-test",
                                                   :namespace "polylith.clj.core.path-finder.path-selector-test",
                                                   :file-path "./components/path-finder/test/polylith/clj/core/path-finder/path_selector_test.clj",
                                                   :imports ["polylith.clj.core.path-finder.path-selector"
                                                             "polylith.clj.core.path-finder.test-data"]}
                                                  {:name "test-data",
                                                   :namespace "polylith.clj.core.path-finder.test-data",
                                                   :file-path "./components/path-finder/test/polylith/clj/core/path-finder/test_data.clj",
                                                   :imports []}
                                                  {:name "path-extractor-test",
                                                   :namespace "polylith.clj.core.path-finder.path-extractor-test",
                                                   :file-path "./components/path-finder/test/polylith/clj/core/path-finder/path_extractor_test.clj",
                                                   :imports ["polylith.clj.core.path-finder.path-extractor"
                                                             "polylith.clj.core.path-finder.test-data"
                                                             "polylith.clj.core.file.interfc"]}
                                                  {:name "dep-extractor-test",
                                                   :namespace "polylith.clj.core.path-finder.dep-extractor-test",
                                                   :file-path "./components/path-finder/test/polylith/clj/core/path-finder/dep_extractor_test.clj",
                                                   :imports ["polylith.clj.core.path-finder.dep-extractor"
                                                             "polylith.clj.core.path-finder.test-data"]}
                                                  {:name "profile-extractor-test",
                                                   :namespace "polylith.clj.core.path-finder.profile-extractor-test",
                                                   :file-path "./components/path-finder/test/polylith/clj/core/path-finder/profile_extractor_test.clj",
                                                   :imports ["polylith.clj.core.path-finder.profile-extractor"]}]}
                              :lib-imports {:src ["clojure.string"],}
                              :interface-deps {:src ["file" "util"],}
                              :lib-deps ["clojure"]}
                             {:name "file",
                              :type "component",
                              :lines-of-code {:src 165, :test 2}
                              :interface {:name "file",
                                          :definitions [{:name "absolute-path", :type "function", :arglist [{:name "path"}]}
                                                        {:name "copy-resource-file!",
                                                         :type "function",
                                                         :arglist [{:name "source"} {:name "target-path"}]}
                                                        {:name "create-dir",
                                                         :type "function",
                                                         :arglist [{:name "path", :type "^String"}]}
                                                        {:name "create-file",
                                                         :type "function",
                                                         :arglist [{:name "path"} {:name "rows"}]}
                                                        {:name "create-missing-dirs",
                                                         :type "function",
                                                         :arglist [{:name "filename"}]}
                                                        {:name "create-temp-dir", :type "function", :arglist [{:name "dir"}]}
                                                        {:name "current-dir", :type "function", :arglist []}
                                                        {:name "delete-dir", :type "function", :arglist [{:name "path"}]}
                                                        {:name "delete-file", :type "function", :arglist [{:name "path"}]}
                                                        {:name "delete-folder", :type "function", :arglist [{:name "file"}]}
                                                        {:name "directory-paths", :type "function", :arglist [{:name "dir"}]}
                                                        {:name "directory?",
                                                         :type "function",
                                                         :arglist [{:name "file", :type "^File"}]}
                                                        {:name "exists", :type "function", :arglist [{:name "path"}]}
                                                        {:name "file-name",
                                                         :type "function",
                                                         :arglist [{:name "file", :type "^File"}]}
                                                        {:name "files-recursively", :type "function", :arglist [{:name "dir-path"}]}
                                                        {:name "lines-of-code", :type "function", :arglist [{:name "file-path"}]}
                                                        {:name "paths-recursively", :type "function", :arglist [{:name "dir"}]}
                                                        {:name "read-file", :type "function", :arglist [{:name "path"}]}
                                                        {:name "relative-paths", :type "function", :arglist [{:name "path"}]}]},
                              :namespaces {:src [{:name "interfc",
                                                  :namespace "polylith.clj.core.file.interfc",
                                                  :file-path "./components/file/src/polylith/clj/core/file/interfc.clj",
                                                  :imports ["polylith.clj.core.file.core"]}
                                                 {:name "core",
                                                  :namespace "polylith.clj.core.file.core",
                                                  :file-path "./components/file/src/polylith/clj/core/file/core.clj",
                                                  :imports ["clojure.java.io" "polylith.clj.core.util.interfc.str"]}]
                                           :test [{:name "core-test",
                                                   :namespace "polylith.clj.core.file.core-test",
                                                   :file-path "./components/file/test/polylith/clj/core/file/core_test.clj",
                                                   :imports []}]}
                              :lib-imports {:src ["clojure.java.io"],}
                              :interface-deps {:src ["util"],}
                              :lib-deps ["clojure"]}
                             {:name "git",
                              :type "component",
                              :lines-of-code {:src 55, :test 18}
                              :interface {:name "git",
                                          :definitions [{:name "add",
                                                         :type "function",
                                                         :arglist [{:name "ws-dir"} {:name "filename"}]}
                                                        {:name "current-sha", :type "function", :arglist [{:name "ws-dir"}]}
                                                        {:name "diff",
                                                         :type "function",
                                                         :arglist [{:name "ws-dir"} {:name "sha1"} {:name "sha2"}]}
                                                        {:name "diff-command",
                                                         :type "function",
                                                         :arglist [{:name "sha1"} {:name "sha2"}]}
                                                        {:name "init", :type "function", :arglist [{:name "ws-dir"}]}]},
                              :namespaces {:src [{:name "interfc",
                                                  :namespace "polylith.clj.core.git.interfc",
                                                  :file-path "./components/git/src/polylith/clj/core/git/interfc.clj",
                                                  :imports ["polylith.clj.core.git.core"]}
                                                 {:name "core",
                                                  :namespace "polylith.clj.core.git.core",
                                                  :file-path "./components/git/src/polylith/clj/core/git/core.clj",
                                                  :imports ["clojure.string" "polylith.clj.core.shell.interfc"]}]
                                           :test [{:name "git-test",
                                                   :namespace "polylith.clj.core.git.git-test",
                                                   :file-path "./components/git/test/polylith/clj/core/git/git_test.clj",
                                                   :imports ["polylith.clj.core.git.interfc"]}]}
                              :lib-imports {:src ["clojure.string"],}
                              :interface-deps {:src ["shell"],}
                              :lib-deps ["clojure"]}
                             {:name "help",
                              :type "component",
                              :lines-of-code {:src 204, :test 0}
                              :interface {:name "help",
                                          :definitions [{:name "print-help",
                                                         :type "function",
                                                         :arglist [{:name "cmd"} {:name "color-mode"}]}]},
                              :namespaces {:src [{:name "deps",
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
                                                  :imports ["polylith.clj.core.util.interfc.color"]}],}
                              :lib-imports {:src ["clojure.string"],}
                              :interface-deps {:src ["util"],}
                              :lib-deps ["clojure"]}
                             {:name "shell",
                              :type "component",
                              :lines-of-code {:src 19, :test 0}
                              :interface {:name "shell",
                                          :definitions [{:name "sh", :type "function", :arglist [{:name "&"} {:name "args"}]}]},
                              :namespaces {:src [{:name "interfc",
                                                  :namespace "polylith.clj.core.shell.interfc",
                                                  :file-path "./components/shell/src/polylith/clj/core/shell/interfc.clj",
                                                  :imports ["polylith.clj.core.shell.core"]}
                                                 {:name "core",
                                                  :namespace "polylith.clj.core.shell.core",
                                                  :file-path "./components/shell/src/polylith/clj/core/shell/core.clj",
                                                  :imports ["clojure.java.shell"]}],}
                              :lib-imports {:src ["clojure.java.shell"]}
                              :interface-deps {},
                              :lib-deps ["clojure"]}
                             {:name "test-helper",
                              :type "component",
                              :lines-of-code {:src 73, :test 0}
                              :interface {:name "test-helper",
                                          :definitions [{:name "content",
                                                         :type "function",
                                                         :arglist [{:name "dir"} {:name "filename"}]}
                                                        {:name "execute-command",
                                                         :type "function",
                                                         :arglist [{:name "current-dir"}
                                                                   {:name "cmd"}
                                                                   {:name "&"}
                                                                   {:name "[arg1 arg2 arg3]"}]}
                                                        {:name "paths", :type "function", :arglist [{:name "dir"}]}
                                                        {:name "root-dir", :type "function", :arglist []}
                                                        {:name "test-setup-and-tear-down",
                                                         :type "function",
                                                         :arglist [{:name "function"}]}
                                                        {:name "user-home", :type "function", :arglist []}]},
                              :namespaces {:src [{:name "interfc",
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
                                                            "polylith.clj.core.workspace.interfc"]}],}
                              :lib-imports {:src ["clojure.stacktrace" "clojure.string"]}
                              :interface-deps {:src ["change" "command" "common" "file" "git" "user-config" "workspace" "workspace-clj"],}
                              :lib-deps ["clojure"]}
                             {:name "test-runner",
                              :type "component",
                              :lines-of-code {:src 108, :test 0}
                              :interface {:name "test-runner",
                                          :definitions [{:name "run", :type "function", :arglist [{:name "workspace"}]}]},
                              :namespaces {:src [{:name "interfc",
                                                  :namespace "polylith.clj.core.test-runner.interfc",
                                                  :file-path "./components/test-runner/src/polylith/clj/core/test_runner/interfc.clj",
                                                  :imports ["polylith.clj.core.test-runner.core"]}
                                                 {:name "core",
                                                  :namespace "polylith.clj.core.test-runner.core",
                                                  :file-path "./components/test-runner/src/polylith/clj/core/test_runner/core.clj",
                                                  :imports ["clojure.string"
                                                            "clojure.tools.deps"
                                                            "polylith.clj.core.common.interfc"
                                                            "polylith.clj.core.util.interfc.color"
                                                            "polylith.clj.core.util.interfc.str"
                                                            "polylith.clj.core.util.interfc.time"]}],}
                              :lib-imports {:src ["clojure.string" "clojure.tools.deps"]}
                              :interface-deps {:src ["common" "util"],}
                              :lib-deps ["clojure" "clojure.tools.deps"]}
                             {:name "text-table",
                              :type "component",
                              :lines-of-code {:src 145, :test 117}
                              :interface {:name "text-table",
                                          :definitions [{:name "line", :type "function", :arglist [{:name "rows"}]}
                                                        {:name "line",
                                                         :type "function",
                                                         :arglist [{:name "rows"} {:name "visables"}]}
                                                        {:name "table",
                                                         :type "function",
                                                         :arglist [{:name "initial-spaces"}
                                                                   {:name "alignments"}
                                                                   {:name "colors"}
                                                                   {:name "rows"}
                                                                   {:name "color-mode"}]}
                                                        {:name "table",
                                                         :type "function",
                                                         :arglist [{:name "initial-spaces"}
                                                                   {:name "alignments"}
                                                                   {:name "header-colors"}
                                                                   {:name "header-orientations"}
                                                                   {:name "colors"}
                                                                   {:name "headers"}
                                                                   {:name "rows"}
                                                                   {:name "color-mode"}]}
                                                        {:name "table",
                                                         :type "function",
                                                         :arglist [{:name "initial-spaces"}
                                                                   {:name "alignments"}
                                                                   {:name "header-colors"}
                                                                   {:name "header-orientations"}
                                                                   {:name "colors"}
                                                                   {:name "headers"}
                                                                   {:name "line-visables"}
                                                                   {:name "rows"}
                                                                   {:name "color-mode"}]}]},
                              :namespaces {:src [{:name "interfc",
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
                                                  :imports ["clojure.core.matrix"]}]
                                           :test [{:name "interfc-test",
                                                   :namespace "polylith.clj.core.text-table.interfc-test",
                                                   :file-path "./components/text-table/test/polylith/clj/core/text_table/interfc_test.clj",
                                                   :imports ["clojure.string"
                                                             "polylith.clj.core.text-table.interfc"
                                                             "polylith.clj.core.util.interfc.color"]}]}
                              :lib-imports {:src ["clojure.core.matrix" "clojure.string"]}
                              :interface-deps {:src ["util"],}
                              :lib-deps ["clojure" "clojure.core.matrix"]}

                             {:name "user-config",
                              :type "component",
                              :lines-of-code {:src 18, :test 0}
                              :interface {:name "user-config",
                                          :definitions [{:name "color-mode", :type "function", :arglist []}
                                                        {:name "config-content", :type "function", :arglist []}
                                                        {:name "home-dir", :type "function", :arglist []}
                                                        {:name "thousand-separator", :type "function", :arglist []}]},
                              :namespaces {:src [{:name "interfc",
                                                  :namespace "polylith.clj.core.user-config.interfc",
                                                  :file-path "./components/user-config/src/polylith/clj/core/user_config/interfc.clj",
                                                  :imports ["polylith.clj.core.util.interfc.str"]}],}
                              :lib-imports {}
                              :interface-deps {:src ["util"],}
                              :lib-deps []}
                             {:name "util",
                              :type "component",
                              :lines-of-code {:src 290, :test 64}
                              :interface {:name "util",
                                          :definitions [{:name "find-first",
                                                         :type "function",
                                                         :arglist [{:name "predicate"} {:name "sequence"}]}
                                                        {:name "first-as-vector", :type "function", :arglist [{:name "vals"}]}
                                                        {:name "ordered-map",
                                                         :type "function",
                                                         :arglist [{:name "&"} {:name "keyvals"}]}
                                                        {:name "stringify-and-sort-map", :type "function", :arglist [{:name "m"}]}
                                                        {:name "def-keys", :type "macro", :arglist [{:name "amap"} {:name "keys"}]}
                                                        {:name "none", :type "data", :sub-ns "color"}
                                                        {:name "base",
                                                         :type "function",
                                                         :arglist [{:name "base"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "blue",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "brick",
                                                         :type "function",
                                                         :arglist [{:name "type"} {:name "brick"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "clean-colors",
                                                         :type "function",
                                                         :arglist [{:name "message"}],
                                                         :sub-ns "color"}
                                                        {:name "colored-text",
                                                         :type "function",
                                                         :arglist [{:name "color"} {:name "color-mode"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "colored-text",
                                                         :type "function",
                                                         :arglist [{:name "color-light"}
                                                                   {:name "color-dark"}
                                                                   {:name "color-mode"}
                                                                   {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "component",
                                                         :type "function",
                                                         :arglist [{:name "component"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "cyan",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "project",
                                                         :type "function",
                                                         :arglist [{:name "project"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "error",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "green",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "grey",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "interface",
                                                         :type "function",
                                                         :arglist [{:name "ifc"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "namespc",
                                                         :type "function",
                                                         :arglist [{:name "namespace"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "namespc",
                                                         :type "function",
                                                         :arglist [{:name "interface"} {:name "namespace"} {:name "color-mode"}],
                                                         :sub-ns "color"}
                                                        {:name "ok",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "purple",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "red",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "warning",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "yellow",
                                                         :type "function",
                                                         :arglist [{:name "color-mode"} {:name "&"} {:name "messages"}],
                                                         :sub-ns "color"}
                                                        {:name "print-error-message",
                                                         :type "function",
                                                         :arglist [{:name "e"}],
                                                         :sub-ns "exception"}
                                                        {:name "print-exception",
                                                         :type "function",
                                                         :arglist [{:name "e"}],
                                                         :sub-ns "exception"}
                                                        {:name "print-stacktrace",
                                                         :type "function",
                                                         :arglist [{:name "e"}],
                                                         :sub-ns "exception"}
                                                        {:name "extract",
                                                         :type "function",
                                                         :arglist [{:name "args"}],
                                                         :sub-ns "params"}
                                                        {:name "key-name",
                                                         :type "function",
                                                         :arglist [{:name "arg"}],
                                                         :sub-ns "params"}
                                                        {:name "named?",
                                                         :type "function",
                                                         :arglist [{:name "arg"}],
                                                         :sub-ns "params"}
                                                        {:name "unnamed?",
                                                         :type "function",
                                                         :arglist [{:name "arg"}],
                                                         :sub-ns "params"}
                                                        {:name "count-things",
                                                         :type "function",
                                                         :arglist [{:name "thing"} {:name "cnt"}],
                                                         :sub-ns "str"}
                                                        {:name "line", :type "function", :arglist [{:name "length"}], :sub-ns "str"}
                                                        {:name "sep-1000",
                                                         :type "function",
                                                         :arglist [{:name "number"} {:name "sep"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-if-ends-with",
                                                         :type "function",
                                                         :arglist [{:name "string"} {:name "ends-with"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-prefix",
                                                         :type "function",
                                                         :arglist [{:name "string"} {:name "prefix"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-suffix",
                                                         :type "function",
                                                         :arglist [{:name "string"} {:name "suffix"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-suffixes",
                                                         :type "function",
                                                         :arglist [{:name "string"} {:name "suffixes"}],
                                                         :sub-ns "str"}
                                                        {:name "skip-until",
                                                         :type "function",
                                                         :arglist [{:name "string"} {:name "separator"}],
                                                         :sub-ns "str"}
                                                        {:name "spaces",
                                                         :type "function",
                                                         :arglist [{:name "n#spaces"}],
                                                         :sub-ns "str"}
                                                        {:name "take-until",
                                                         :type "function",
                                                         :arglist [{:name "string"} {:name "separator"}],
                                                         :sub-ns "str"}
                                                        {:name "current-time", :type "function", :arglist [], :sub-ns "time"}
                                                        {:name "print-execution-time",
                                                         :type "function",
                                                         :arglist [{:name "start-time"}],
                                                         :sub-ns "time"}]},
                              :namespaces {:src [{:name "str",
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
                                                  :imports ["clojure.string"]}]
                                           :test [{:name "util-test",
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
                                                   :imports ["polylith.clj.core.util.interfc.str"]}]}
                              :lib-imports {:src ["clojure.stacktrace" "clojure.string"]}
                              :interface-deps {},
                              :lib-deps ["clojure"]}
                             {:name "validator",
                              :type "component",
                              :lines-of-code {:src 420, :test 810}
                              :interface {:name "validator",
                                          :definitions [{:name "messages",
                                                         :type "function",
                                                         :arglist [{:name "ws-dir"}
                                                                   {:name "suffixed-top-ns"}
                                                                   {:name "interface-names"}
                                                                   {:name "interfaces"}
                                                                   {:name "components"}
                                                                   {:name "bases"}
                                                                   {:name "projects"}
                                                                   {:name "interface-ns"}
                                                                   {:name "ns-to-lib"}
                                                                   {:name "color-mode"}]}]},
                              :namespaces {:src [{:name "m104-circular-deps",
                                                  :namespace "polylith.clj.core.validate.m104-circular-deps",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m104_circular_deps.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"]}
                                                 {:name "m105-illegal-name-sharing",
                                                  :namespace "polylith.clj.core.validate.m105-illegal-name-sharing",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m105_illegal_name_sharing.clj",
                                                  :imports ["clojure.set"
                                                            "clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"]}
                                                 {:name "m107-missing-componens-in-project",
                                                  :namespace "polylith.clj.core.validate.m107-missing-componens-in-project",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m107_missing_componens_in_project.clj",
                                                  :imports ["clojure.set"
                                                            "clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"]}
                                                 {:name "m102-function-or-macro-is-defined-twice",
                                                  :namespace "polylith.clj.core.validate.m102-function-or-macro-is-defined-twice",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m102_function_or_macro_is_defined_twice.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"
                                                            "polylith.clj.core.validate.shared"]}
                                                 {:name "m201-mismatching-arguments",
                                                  :namespace "polylith.clj.core.validate.m201-mismatching-arguments",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m201_mismatching_arguments.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"
                                                            "polylith.clj.core.validate.shared"]}
                                                 {:name "interfc",
                                                  :namespace "polylith.clj.core.validate.interfc",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/interfc.clj",
                                                  :imports ["polylith.clj.core.validate.m101-illegal-namespace-deps"
                                                            "polylith.clj.core.validate.m102-function-or-macro-is-defined-twice"
                                                            "polylith.clj.core.validate.m103-missing-defs"
                                                            "polylith.clj.core.validate.m104-circular-deps"
                                                            "polylith.clj.core.validate.m105-illegal-name-sharing"
                                                            "polylith.clj.core.validate.m106-multiple-interface-occurrences"
                                                            "polylith.clj.core.validate.m107-missing-componens-in-project"
                                                            "polylith.clj.core.validate.m201-mismatching-arguments"
                                                            "polylith.clj.core.validate.m202-missing-libraries"
                                                            "polylith.clj.core.validate.m203-invalid-src-reference"]}
                                                 {:name "m202-missing-libraries",
                                                  :namespace "polylith.clj.core.validate.m202-missing-libraries",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m202_missing_libraries.clj",
                                                  :imports ["clojure.set"
                                                            "clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"]}
                                                 {:name "m106-multiple-interface-occurrences",
                                                  :namespace "polylith.clj.core.validate.m106-multiple-interface-occurrences",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m106_multiple_interface_occurrences.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"]}
                                                 {:name "m101-illegal-namespace-deps",
                                                  :namespace "polylith.clj.core.validate.m101-illegal-namespace-deps",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m101_illegal_namespace_deps.clj",
                                                  :imports ["polylith.clj.core.common.interfc"
                                                            "polylith.clj.core.deps.interfc"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"]}
                                                 {:name "shared",
                                                  :namespace "polylith.clj.core.validate.shared",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/shared.clj",
                                                  :imports ["clojure.string"]}
                                                 {:name "m103-missing-defs",
                                                  :namespace "polylith.clj.core.validate.m103-missing-defs",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m103_missing_defs.clj",
                                                  :imports ["clojure.set"
                                                            "clojure.string"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"
                                                            "polylith.clj.core.validate.shared"]}
                                                 {:name "m203-invalid-src-reference",
                                                  :namespace "polylith.clj.core.validate.m203-invalid-src-reference",
                                                  :file-path "./components/validator/src/polylith/clj/core/validator/m203_invalid_src_reference.clj",
                                                  :imports ["clojure.string"
                                                            "polylith.clj.core.file.interfc"
                                                            "polylith.clj.core.util.interfc"
                                                            "polylith.clj.core.util.interfc.color"
                                                            "polylith.clj.core.util.interfc.str"]}]
                                           :test [{:name "m102-duplicated-arglists-test",
                                                   :namespace "polylith.clj.core.validate.m102-duplicated-arglists-test",
                                                   :file-path "./components/validator/test/polylith/clj/core/validator/m102_duplicated_arglists_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m102-function-or-macro-is-defined-twice"]}
                                                  {:name "m101-illegal-namespace-deps-test",
                                                   :namespace "polylith.clj.core.validate.m101-illegal-namespace-deps-test",
                                                   :file-path "./components/validator/test/polylith/clj/core/validator/m101_illegal_namespace_deps_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m101-illegal-namespace-deps"]}
                                                  {:name "m203-invalid-src-reference-test",
                                                   :namespace "polylith.clj.core.validate.m203-invalid-src-reference-test",
                                                   :file-path "./components/validator/test/polylith/clj/core/validator/m203_invalid_src_reference_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m203-invalid-src-reference"]}
                                                  {:name "m105-illegal-name-sharing-test",
                                                   :namespace "polylith.clj.core.validate.m105-illegal-name-sharing-test",
                                                   :file-path "./components/validator/test/polylith/clj/core/validator/m105_illegal_name_sharing_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m105-illegal-name-sharing"]}
                                                  {:name "m104-circular-deps-test",
                                                   :namespace "polylith.clj.core.validate.m104-circular-deps-test",
                                                   :file-path "./components/validator/test/polylith/clj/core/validator/m104_circular_deps_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m104-circular-deps"]}
                                                  {:name "m106-multiple-interface-occurrences-test",
                                                   :namespace "polylith.clj.core.validate.m106-multiple-interface-occurrences-test",
                                                   :file-path "./components/validate/test/polylith/clj/core/validate/m106_multiple_interface_occurrences_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m106-multiple-interface-occurrences"]}
                                                  {:name "m201-mismatching-arguments-test",
                                                   :namespace "polylith.clj.core.validate.m201-mismatching-arguments-test",
                                                   :file-path "./components/validate/test/polylith/clj/core/validate/m201_mismatching_arguments_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m201-mismatching-arguments"]}
                                                  {:name "m103-missing-defs-test",
                                                   :namespace "polylith.clj.core.validate.m103-missing-defs-test",
                                                   :file-path "./components/validate/test/polylith/clj/core/validate/m103_missing_defs_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m103-missing-defs"]}
                                                  {:name "m107-missing-componens-in-project-test",
                                                   :namespace "polylith.clj.core.validate.m107-missing-componens-in-project-test",
                                                   :file-path "./components/validate/test/polylith/clj/core/validate/m107_missing_componens_in_project_test.clj",
                                                   :imports ["polylith.clj.core.util.interfc.color"
                                                             "polylith.clj.core.validate.m107-missing-componens-in-project"]}
                                                  {:name "m202-missing-libraries-test",
                                                   :namespace "polylith.clj.core.validate.m202-missing-libraries-test",
                                                   :file-path "./components/validate/test/polylith/clj/core/validate/m202_missing_libraries_test.clj",
                                                   :imports ["polylith.clj.core.validate.m202-missing-libraries"]}]}
                              :lib-imports {:src ["clojure.set" "clojure.string"]}
                              :interface-deps {:src ["common" "deps" "file" "util"],}
                              :lib-deps ["clojure"]}
                             {:name "workspace",
                              :type "component",
                              :lines-of-code {:src 844, :test 1008}
                              :interface {:name "workspace",
                                          :definitions [{:name "enrich-workspace",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "user-input"}]}
                                                        {:name "enrich-workspace-str-keys",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "user-input"}]}
                                                        {:name "print-info",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "is-show-loc"}]}
                                                        {:name "print-table-str-keys",
                                                         :type "function",
                                                         :arglist [{:name "workspace"} {:name "is-show-loc"}]}]},
                              :lib-imports {:src ["clojure.set" "clojure.string" "clojure.walk"]}
                              :lib-deps ["clojure"]}
                             {:name "workspace-clj",
                              :type "component",
                              :lines-of-code {:src 324, :test 150}
                              :interface {:name "workspace-clj",
                                          :definitions [{:name "workspace-from-disk", :type "function", :arglist [{:name "ws-dir"}]}
                                                        {:name "workspace-from-disk",
                                                         :type "function",
                                                         :arglist [{:name "ws-dir"} {:name "config"}]}]},
                              :namespaces {:src [{:name "interface-defs-from-disk",
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
                                                 {:name "project-from-disk",
                                                  :namespace "polylith.clj.core.workspace-clj.project-from-disk",
                                                  :file-path "./components/workspace-clj/src/polylith/clj/core/workspace_clj/project_from_disk.clj",
                                                  :imports ["clojure.string"
                                                            "clojure.tools.deps.util.maven"
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
                                                            "polylith.clj.core.workspace-clj.project-from-disk"
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
                                                            "polylith.clj.core.workspace-clj.namespaces-from-disk"]}]
                                           :test [{:name "import-from-disk-test",
                                                   :namespace "polylith.clj.core.workspace-clj.import-from-disk-test",
                                                   :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/import_from_disk_test.clj",
                                                   :imports ["polylith.clj.core.workspace-clj.namespaces-from-disk"]}
                                                  {:name "readimportsfromdisk-test",
                                                   :namespace "polylith.clj.core.workspace-clj.readimportsfromdisk-test",
                                                   :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/readimportsfromdisk_test.clj",
                                                   :imports ["polylith.clj.core.workspace-clj.namespaces-from-disk"]}
                                                  {:name "project-test",
                                                   :namespace "polylith.clj.core.workspace-clj.project-test",
                                                   :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/project_test.clj",
                                                   :imports ["clojure.tools.deps.util.maven"
                                                             "polylith.clj.core.file.interfc"
                                                             "polylith.clj.core.workspace-clj.project-from-disk"]}
                                                  {:name "definitions-test",
                                                   :namespace "polylith.clj.core.workspace-clj.definitions-test",
                                                   :file-path "./components/workspace-clj/test/polylith/clj/core/workspace_clj/definitions_test.clj",
                                                   :imports ["polylith.clj.core.workspace-clj.definitions"]}]}
                              :lib-imports {:src ["clojure.string" "clojure.tools.deps.util.maven"]
                                            :test ["clojure.tools.deps.util.maven"]}
                              :interface-deps {:src ["common" "file" "user-config" "util"],}
                              :lib-deps ["clojure" "clojure.tools.deps"]}],
                :changes {:sha1 "HEAD",
                          :git-diff-command "git diff HEAD --name-only",
                          :changed-components ["path-finder" "workspace"],
                          :changed-bases [],
                          :changed-projects [],
                          :project-to-indirect-changes {"poly" ["poly-cli" "command" "test-helper"],
                                                        "core" ["poly-cli" "command" "test-helper"],
                                                        "development" ["poly-cli" "command" "test-helper"]},
                          :project-to-bricks-to-test {"poly" ["command" "path-finder" "workspace"], "core" [], "development" []},
                          :projects-to-test [],
                          :changed-files ["components/path-finder/src/polylith/clj/core/path-finder/project_statuses.clj"
                                          "components/path-finder/src/polylith/clj/core/path-finder/interfc.clj"
                                          "components/path-finder/src/polylith/clj/core/path-finder/matchers.clj"
                                          "components/path-finder/src/polylith/clj/core/path-finder/status.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/core.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/text_table/new_project_table.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/text_table/ws_table.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/ws_table/core.clj"
                                          "components/workspace/src/polylith/clj/core/workspace/ws_table/project_columns.clj"
                                          "components/workspace/test/polylith/clj/core/workspace/text_table/project_table_test.clj"
                                          "components/workspace/test/polylith/clj/core/workspace/text_table/ws_table_test.clj"
                                          "development/src/dev/jocke.clj"
                                          "todo.txt"]},
                :bases [{:name "poly-cli",
                         :type "base",
                         :lines-of-code {:src 22, :test 0}
                         :namespaces {:src [{:name "poly",
                                             :namespace "polylith.clj.core.poly-cli.core",
                                             :file-path "./bases/poly-cli/src/polylith/clj/core/poly_cli/poly.clj",
                                             :imports ["polylith.clj.core.change.interfc"
                                                       "polylith.clj.core.command.interfc"
                                                       "polylith.clj.core.common.interfc"
                                                       "polylith.clj.core.file.interfc"
                                                       "polylith.clj.core.util.interfc.exception"
                                                       "polylith.clj.core.workspace-clj.interfc"
                                                       "polylith.clj.core.workspace.interfc"]}],}
                         :lib-imports {}
                         :interface-deps {:src ["change" "command" "common" "file" "util" "workspace" "workspace-clj"],}
                         :lib-deps []}]})

(deftest ws-table--without-loc-info--return-table-without-loc-info
  (with-redefs [file/exists (fn [_] true)]
    (is (= ["  interface      brick           poly  core   dev"
            "  ----------------------------   ----------   ---"
            "  change         change          st-   s--    st-"
            "  command        command         stx   ---    st-"
            "  common         common          st-   s--    st-"
            "  creator        creator         st-   ---    ---"
            "  deps           deps            st-   s--    st-"
            "  file           file            st-   s--    ---"
            "  git            git             st-   s--    st-"
            "  help           help            st-   s--    st-"
            "  path-finder    path-finder *   stx   s--    st-"
            "  shell          shell           st-   s--    st-"
            "  test-helper    test-helper     st-   ---    st-"
            "  test-runner    test-runner     st-   ---    st-"
            "  text-table     text-table      st-   s--    st-"
            "  user-config    user-config     st-   s--    st-"
            "  util           util            st-   s--    st-"
            "  validator      validator       st-   s--    st-"
            "  workspace      workspace *     stx   s--    st-"
            "  workspace-clj  workspace-clj   st-   ---    st-"
            "  -              poly-cli        st-   ---    st-"]
           (ws-table/table workspace false false)))))

(deftest ws-table--with-loc-info--return-table-with-loc-info
  (with-redefs [file/exists (fn [_] true)]
    (is (= ["  interface      brick           poly   core     dev      loc   (t)"
            "  ----------------------------   ------------   -----   -----------"
            "  change         change          s-t-   s---    s-t-      134   343"
            "  command        command         s-tx   ----    s-t-      151     0"
            "  common         common          s-t-   s---    s-t-      336    53"
            "  creator        creator         srt-   ----    ----      181   282"
            "  deps           deps            s-t-   s---    s-t-      242   328"
            "  file           file            s-t-   s---    ----      165     2"
            "  git            git             s-t-   s---    s-t-       55    18"
            "  help           help            s-t-   s---    s-t-      204     0"
            "  path-finder    path-finder *   s-tx   s---    s-t-      591   343"
            "  shell          shell           s-t-   s---    s-t-       19     0"
            "  test-helper    test-helper     s-t-   ----    s-t-       73     0"
            "  test-runner    test-runner     s-t-   ----    s-t-      108     0"
            "  text-table     text-table      s-t-   s---    s-t-      145   117"
            "  user-config    user-config     s-t-   s---    s-t-       18     0"
            "  util           util            s-t-   s---    s-t-      290    64"
            "  validator      validator       s-t-   s---    s-t-      420   810"
            "  workspace      workspace *     s-tx   s---    s-t-      844 1,008"
            "  workspace-clj  workspace-clj   s-t-   ----    s-t-      324   150"
            "  -              poly-cli        s-t-   ----    s-t-       22     0"
            "                                 4,322  3,463   3,976   4,322 3,518"]
           (ws-table/table workspace true true)))))
