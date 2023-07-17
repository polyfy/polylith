(ns polylith.clj.core.shell.candidate.select-candidates-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.system.interface :as system]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.shell.candidate.setup :as setup]
            [polylith.clj.core.shell.candidate.engine :as engine])

  (:refer-clojure :exclude [test]))

(use-fixtures :each setup/reset-ws)

(def candidates setup/candidates)

(defn clean-colors [entities]
  (mapv color/clean-colors entities))

(deftest all-commands
  (is (= (candidates "")
         (concat ["check" "create" "deps" "diff" "help" "info" "libs"]
                 (if system/extended? ["overview"] [])
                 ["switch-ws" "test" "version" "ws"]))))

(deftest check
  (is (= (candidates "check")
         [])))

(deftest create
  (is (= (candidates "create")
         ["base" "component" "project"])))

(deftest create-
  (is (= (candidates "create" :next "")
         ["base" "component" "project"])))

(deftest create-b
  (is (= (candidates "create" :next "b")
         ["base"])))

(deftest create-base
  (is (= (candidates "create" :next "base")
         ["name"])))

(deftest create-base-n
  (is (= (candidates "create" :next "base" :next "n")
         ["name"])))

(deftest create-base-x
  (is (= (candidates "create" :next "base" :next "n")
         ["name"])))

(deftest create-base-name
  (is (= (candidates "create" :next "base" :next "name")
         [])))

(deftest create-component
  (is (= (candidates "create" :next "component")
         ["interface" "name"])))

(deftest create-component-
  (is (= (candidates "create" :next "component" :next "")
         ["name"])))

(deftest create-component-name
  (is (= (candidates "create" :next "component" :next "name" "")
         [])))

(deftest create-component-name-c1
  (is (= (candidates "create" :next "component" :next "name" "c1")
         [])))

(deftest create-component-name-c1-
  (is (= (candidates "create" :next "component" :next "name" "c1" :next "")
         ["interface"])))

(deftest create-component-name-c1-interface
  (is (= (candidates "create" :next "component" :next "name" "c1" :next "interface")
         [""])))

(deftest create-component-name-c1-interface-i1
  (is (= (candidates "create" :next "component" :next "name" "c1" :next "interface" "i1")
         [])))

(deftest deps
  (is (= (candidates "deps")
         ["brick" "project"])))

(deftest deps-brick
  (is (= (candidates "deps" :next "brick")
         [])))

(deftest deps-brick-
  (is (= (clean-colors (candidates "deps" :next "brick" ""))
         ["api"
          "change"
          "command"
          "common"
          "creator"
          "deployer"
          "deps"
          "file"
          "git"
          "help"
          "lib"
          "migrator"
          "path-finder"
          "sh"
          "shell"
          "test-helper"
          "test-runner"
          "text-table"
          "user-config"
          "user-input"
          "util"
          "validator"
          "version"
          "workspace"
          "workspace-clj"
          "ws-explorer"
          "ws-file"
          "deployer-cli"
          "poly-cli"])))

(deftest deps-brick-test
  (is (= (mapv color/clean-colors (candidates "deps" :next "brick" "test"))
         ["test-helper" "test-runner"])))

(deftest deps-brick-test-helper
  (is (= (candidates "deps" :next "brick" "test-helper" :next "")
         ["project"])))

(deftest deps-brick-help
  (is (= (candidates "deps" :next "brick" "help")
         [])))

(deftest deps-brick-file-
  (is (= (candidates "deps" :next "brick" "file" :next "")
         ["project"])))

(deftest deps-brick-deployer-project-
  (is (= (clean-colors (candidates "deps" :next "brick" "deployer" :next "project" ""))
         ["deployer" "development"])))

(deftest deps-brick-help-project-poly
  (is (= (candidates "deps" :next "brick" "help" :next "project" "poly")
         [])))

(deftest deps-project
  (is (= (candidates "deps" :next "project")
         [])))

(deftest deps-project-
  (is (= (clean-colors (candidates "deps" :next "project" ""))
         ["api" "core" "deployer" "development" "poly"])))

(deftest deps-project-api
  (is (= (candidates "deps" :next "project" "api")
         [])))

(deftest deps-project-api-
  (is (= (candidates "deps" :next "project" "api" :next)
         ["brick"])))

(deftest deps-project-deployer-brick-
  (is (= (clean-colors (candidates "deps" :next "project" "deployer" :next "brick" ""))
         ["api"
          "change"
          "common"
          "deployer"
          "deps"
          "file"
          "git"
          "lib"
          "path-finder"
          "sh"
          "text-table"
          "user-config"
          "user-input"
          "util"
          "validator"
          "version"
          "workspace"
          "workspace-clj"
          "ws-explorer"
          "deployer-cli"])))

(deftest deps-project-api-brick-file-
  (is (= (candidates "deps" :next "project" "api" :next "brick" "file" :next)
         [])))

(deftest diff
  (is (= (candidates "diff")
         ["since"])))

(deftest help
  (is (= (candidates "help")
         ["check" "create" "deps" "diff" "info" "libs" "shell" "switch-ws"
          "tap" "test" "version" "ws"])))

(deftest help-check
  (is (= (candidates "help" :next "check")
         [])))

(deftest help-create
  (is (= (candidates "help" :next "create")
         ["base" "component" "project" "workspace"])))

(deftest help-create-base
  (is (= (candidates "help" :next "create" :next "base")
         [])))

(deftest help-create-component
  (is (= (candidates "help" :next "create" :next "component")
         [])))

(deftest help-create-project
  (is (= (candidates "help" :next "create" :next "project")
         [])))

(deftest help-create-workspace
  (is (= (candidates "help" :next "create" :next "workspace")
         [])))

(deftest help-deps
  (is (= (candidates "help" :next "deps")
         [":brick" ":project" ":workspace"])))

(deftest help-deps-brick-
  (is (= (candidates "help" :next "deps" :next "brick" "")
         [":project" ":workspace"])))

(deftest help-deps-brick-project
  (is (= (candidates "help" :next "deps" :next "brick" :next "project")
         [])))

(deftest help-deps-project-
  (is (= (candidates "help" :next "deps" :next "project" "")
         [":brick" ":workspace"])))

(deftest help-deps-project-brick
  (is (= (candidates "help" :next "deps" :next "project" :next "brick")
         [])))

(deftest help-diff
  (is (= (candidates "help" :next "diff")
         [])))

(deftest help-info
  (is (= (candidates "help" :next "info")
         [])))

(deftest help-libs
  (is (= (candidates "help" :next "libs")
         [])))

(deftest help-test
  (is (= (candidates "help" :next "test")
         [])))

(deftest help-version
  (is (= (candidates "help" :next "version")
         [])))

(deftest help-ws
  (is (= (candidates "help" :next "ws")
         [])))

(deftest info
  (is (= (candidates "info")
         [":all" ":all-bricks" ":dev" ":loc" ":project" ":resources" "brick"
          "project" "since"])))

(deftest info-loc-
  (is (= (candidates "info" :next "loc" :next "")
         [":all" ":all-bricks" ":dev" ":project" ":resources" "brick"
          "project" "since"])))

(deftest info-wsfile-go-back-4-dirs-
  (is (= (contains? (set (candidates "info" :next "ws-file" ".." ".." ".." ".." ""))
                    "../")
         false)))

(deftest libs
  (is (= (candidates "libs")
         [":outdated"])))

(deftest libs-
  (is (= (candidates "libs" :next "")
         [":outdated"])))

(deftest test
  (is (= (candidates "test")
         [":all" ":all-bricks" ":dev" ":loc" ":project" ":verbose" "brick" "project" "since"])))

(deftest test-:project-
  (is (= (candidates "test" :next ":project" :next "")
         [":all" ":all-bricks" ":dev" ":loc" ":verbose" "brick" "project" "since"])))

(deftest test-project-
  (is (= (candidates "test" :next "project" "poly" :next "")
         [":all" ":all-bricks" ":dev" ":loc" ":project" ":verbose" "brick" "since"])))

(deftest test-loc-
  (is (= (candidates "test" :next "loc" :next "")
         [":all" ":all-bricks" ":dev" ":project" ":verbose" "brick" "project" "since"])))

(deftest version
  (is (= (candidates "version")
         [])))

(deftest ws
  (is (= (candidates "ws")
         [":all" ":all-bricks" ":dev"  ":latest-sha" ":loc" ":project" "branch"
          "brick" "get" "out" "project" "since"])))

(deftest ws-
  (is (= (candidates "ws" :next "")
         [":all" ":all-bricks" ":dev"  ":latest-sha" ":loc" ":project" "branch"
          "brick" "get" "out" "project" "since"])))

(deftest ws-get
  (is (= (candidates "ws" :next "get" "")
         ["bases" "changes" "components" "interfaces" "messages" "name" "paths"
          "projects" "settings" "user-input" "version" "ws-dir" "ws-reader"])))

(deftest ws-get-bases
  (is (= (candidates "ws" :next "get" "bases" "")
         ["deployer-cli"
          "poly-cli"])))

(deftest ws-get-bases-polycli
  (is (= (candidates "ws" :next "get" "bases" "poly-cli" "")
         ["interface-deps" "lib-deps" "lib-imports" "lines-of-code" "name"
          "namespaces" "paths" "type"])))

(deftest ws-get-bases-polycli-linesofcode-src
  (is (= (candidates "ws" :next "get" "bases" "poly-cli" "lines-of-code" "src" "")
         [])))

(deftest ws-get-components-shell-interface
  (is (= (candidates "ws" :next "get" "components" "shell" "interface")
         ["interface" "interface-deps"])))

(deftest ws-get-components-shell-interface-
  (is (= (candidates "ws" :next "get" "components" "shell" "interface" "")
         ["definitions" "name"])))

(deftest ws-get-components-shell-interface-definitions-
  (is (= (candidates "ws" :next "get" "components" "shell" "interface" "definitions" "")
         ["start"])))

(deftest ws-get-components-api-non-top-namespaces-
  (is (= (setup/candidates "ws" :next "get" "components" "api" "non-top-namespaces" "")
         [])))

(deftest ws-out-components-next-
  (is (= (candidates "ws" :next "out" "components" :next "")
         [":all" ":all-bricks" ":dev" ":latest-sha" ":loc" ":project" "branch"
          "brick" "get" "project" "since"])))

(deftest ws-out-parentdir-
  (is (= (contains? (set (candidates "ws" :next "out" ".." ""))
                    "polylith/")
         true)))

(deftest ws-out-missing-
  (is (= (candidates "ws" :next "out" "missing" "")
         [])))

(deftest doc-example-profile
  (reset! engine/ws (-> "examples/doc-example/ws.edn" slurp read-string))
  (is (= (candidates "info" :next
                     "+default" :next
                     "loc" :next
                     "dev" :next
                     "all" :next
                     "all-bricks" :next)
         ["+remote" ":project" ":resources" "brick"  "project" "since"])))
