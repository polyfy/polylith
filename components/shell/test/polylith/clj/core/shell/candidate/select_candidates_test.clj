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
  (is (= (concat ["check" "create" "deps" "diff" "doc" "help" "info" "libs"]
                 (if system/extended? ["overview"] [])
                 ["switch-ws" "test" "version" "ws"])
         (candidates ""))))

(deftest check
  (is (= []
         (candidates "check"))))

(deftest create
  (is (= ["base" "component" "project"]
         (candidates "create"))))

(deftest create-
  (is (= ["base" "component" "project"]
         (candidates "create" :next ""))))

(deftest create-b
  (is (= ["base"]
         (candidates "create" :next "b"))))

(deftest create-base
  (is (= ["dialect" "name"]
         (candidates "create" :next "base"))))

(deftest create-base-n
  (is (= ["name"]
         (candidates "create" :next "base" :next "n"))))

(deftest create-base-x
  (is (= ["name"]
         (candidates "create" :next "base" :next "n"))))

(deftest create-base-name
  (is (= [""]
         (candidates "create" :next "base" :next "name"))))

(deftest create-component
  (is (= ["dialect" "interface" "name"]
         (candidates "create" :next "component"))))

(deftest create-component-
  (is (= ["name"]
         (candidates "create" :next "component" :next ""))))

(deftest create-component-name
  (is (= []
         (candidates "create" :next "component" :next "name" ""))))

(deftest create-component-name-c1
  (is (= []
         (candidates "create" :next "component" :next "name" "c1"))))

(deftest create-component-name-c1-
  (is (= ["dialect" "interface"]
         (candidates "create" :next "component" :next "name" "c1" :next ""))))

(deftest create-component-name-c1-interface
  (is (= [""]
         (candidates "create" :next "component" :next "name" "c1" :next "interface"))))

(deftest create-component-name-c1-interface-i1
  (is (= []
         (candidates "create" :next "component" :next "name" "c1" :next "interface" "i1"))))

(deftest deps
  (is (= [":swap-axes" "brick" "color-mode" "project"]
         (candidates "deps"))))

(deftest deps-brick
  (is (= []
         (candidates "deps" :next "brick"))))

(deftest deps-brick-
  (is (= ["api"
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
          "poly-cli"]
         (clean-colors (candidates "deps" :next "brick" "")))))

(deftest deps-brick-test
  (is (= ["test-helper" "test-runner"]
         (mapv color/clean-colors (candidates "deps" :next "brick" "test")))))

(deftest deps-brick-test-helper
  (is (= [":swap-axes" "color-mode" "project"]
         (candidates "deps" :next "brick" "test-helper" :next ""))))

(deftest deps-brick-help
  (is (= []
         (candidates "deps" :next "brick" "help"))))

(deftest deps-brick-file-
  (is (= [":swap-axes" "color-mode" "project"]
         (candidates "deps" :next "brick" "file" :next ""))))

(deftest deps-brick-deployer-project-
  (is (= ["deployer" "development"]
         (clean-colors (candidates "deps" :next "brick" "deployer" :next "project" "")))))

(deftest deps-brick-help-project-poly
  (is (= []
         (candidates "deps" :next "brick" "help" :next "project" "poly"))))

(deftest deps-project
  (is (= []
         (candidates "deps" :next "project"))))

(deftest deps-project-
  (is (= ["api" "core" "deployer" "development" "poly"]
         (clean-colors (candidates "deps" :next "project" "")))))

(deftest deps-project-api
  (is (= []
         (candidates "deps" :next "project" "api"))))

(deftest deps-project-api-
  (is (= [":swap-axes" "brick" "color-mode"]
         (candidates "deps" :next "project" "api" :next))))

(deftest deps-project-deployer-brick-
  (is (= ["api"
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
          "deployer-cli"]
         (clean-colors (candidates "deps" :next "project" "deployer" :next "brick" "")))))

(deftest deps-project-api-brick-file-
  (is (= [":swap-axes" "color-mode"]
         (candidates "deps" :next "project" "api" :next "brick" "file" :next))))

(deftest diff
  (is (= ["since"]
         (candidates "diff"))))

(deftest doc
  (is (= ["help" "more" "page" "ws"]
         (candidates "doc"))))

(deftest doc-command-deps
  (is (= ["more" "page" "ws"]
         (candidates "doc" :next "help" "deps" :next))))

(deftest doc-move-videos-polylith-in-a-nutshell
  (is (= ["polylith-in-a-nutshell"]
         (candidates "doc" :next "more" "videos" "polylith-in"))))

(deftest doc-page-git
  (is (= ["help" "more" "ws"]
         (candidates "doc" :next "page" "git" :next))))

(deftest doc-ws-settings
  (is (= ["help" "more" "page"]
         (candidates "doc" :next "ws" "settings" :next))))

(deftest help
  (is (= ["check" "create" "deps" "diff" "info" "libs" "shell" "switch-ws"
          "tap" "test" "version" "ws"]
         (candidates "help"))))

(deftest help-check
  (is (= []
         (candidates "help" :next "check"))))

(deftest help-create
  (is (= ["base" "component" "project" "workspace"]
         (candidates "help" :next "create"))))

(deftest help-create-base
  (is (= []
         (candidates "help" :next "create" :next "base"))))

(deftest help-create-component
  (is (= []
         (candidates "help" :next "create" :next "component"))))

(deftest help-create-project
  (is (= []
         (candidates "help" :next "create" :next "project"))))

(deftest help-create-workspace
  (is (= []
         (candidates "help" :next "create" :next "workspace"))))

(deftest help-deps
  (is (= [":brick" ":project" ":workspace"]
         (candidates "help" :next "deps"))))

(deftest help-deps-brick-
  (is (= [":project" ":workspace"]
         (candidates "help" :next "deps" :next "brick" ""))))

(deftest help-deps-brick-project
  (is (= []
         (candidates "help" :next "deps" :next "brick" :next "project"))))

(deftest help-deps-project-
  (is (= [":brick" ":workspace"]
         (candidates "help" :next "deps" :next "project" ""))))

(deftest help-deps-project-brick
  (is (= []
         (candidates "help" :next "deps" :next "project" :next "brick"))))

(deftest help-diff
  (is (= []
         (candidates "help" :next "diff"))))

(deftest help-info
  (is (= []
         (candidates "help" :next "info"))))

(deftest help-libs
  (is (= []
         (candidates "help" :next "libs"))))

(deftest help-test
  (is (= []
         (candidates "help" :next "test"))))

(deftest help-version
  (is (= []
         (candidates "help" :next "version"))))

(deftest help-ws
  (is (= []
         (candidates "help" :next "ws"))))

(deftest info
  (is (= [":all" ":all-bricks" ":dev" ":loc" ":project" ":resources" "brick"
          "color-mode" "project" "since"]
         (candidates "info"))))

(deftest info-loc-
  (is (= [":all" ":all-bricks" ":dev" ":project" ":resources" "brick"
          "color-mode" "project" "since"]
         (candidates "info" :next "loc" :next ""))))

(deftest info-wsfile-go-back-4-dirs-
  (is (= false
         (contains? (set (candidates "info" :next "ws-file" ".." ".." ".." ".." ""))
                    "../"))))

(deftest libs
  (is (= [":outdated" ":update" "color-mode" "libraries"]
         (candidates "libs"))))

(deftest libs-
  (is (= [":outdated" ":update" "color-mode" "libraries"]
         (candidates "libs" :next ""))))

(deftest test
  (is (= [":all" ":all-bricks" ":dev" ":loc" ":project" ":verbose" "brick" "project" "since"]
         (candidates "test"))))

(deftest test-:project-
  (is (= [":all" ":all-bricks" ":dev" ":loc" ":verbose" "brick" "project" "since"]
         (candidates "test" :next ":project" :next ""))))

(deftest test-project-
  (is (= [":all" ":all-bricks" ":dev" ":loc" ":project" ":verbose" "brick" "since"]
         (candidates "test" :next "project" "poly" :next ""))))

(deftest test-loc-
  (is (= [":all" ":all-bricks" ":dev" ":project" ":verbose" "brick" "project" "since"]
         (candidates "test" :next "loc" :next ""))))

(deftest version
  (is (= []
         (candidates "version"))))

(deftest ws
  (is (= [":all" ":all-bricks" ":dev"  ":latest-sha" ":loc" ":project"
          "brick" "get" "out" "project" "since"]
         (candidates "ws"))))

(deftest ws-
  (is (= [":all" ":all-bricks" ":dev"  ":latest-sha" ":loc" ":project"
          "brick" "get" "out" "project" "since"]
         (candidates "ws" :next ""))))

(deftest ws-get
  (is (= ["bases" "changes" "components" "interfaces" "messages" "name" "paths"
          "profiles" "projects" "settings" "user-input" "version" "ws-dir" "ws-reader"]
         (candidates "ws" :next "get" ""))))

(deftest ws-get-bases
  (is (= ["deployer-cli"
          "poly-cli"]
         (candidates "ws" :next "get" "bases" ""))))

(deftest ws-get-bases-polycli
  (is (= ["interface-deps" "lib-deps" "lib-imports" "lines-of-code" "name"
          "namespaces" "paths" "type"]
         (candidates "ws" :next "get" "bases" "poly-cli" ""))))

(deftest ws-get-bases-polycli-linesofcode-src
  (is (= []
         (candidates "ws" :next "get" "bases" "poly-cli" "lines-of-code" "src" ""))))

(deftest ws-get-components-shell-interface
  (is (= ["interface" "interface-deps"]
         (candidates "ws" :next "get" "components" "shell" "interface"))))

(deftest ws-get-components-shell-interface-
  (is (= ["definitions" "name"]
         (candidates "ws" :next "get" "components" "shell" "interface" ""))))

(deftest ws-get-components-shell-interface-definitions-
  (is (= ["start"]
         (candidates "ws" :next "get" "components" "shell" "interface" "definitions" ""))))

(deftest ws-get-components-api-non-top-namespaces-
  (is (= []
         (setup/candidates "ws" :next "get" "components" "api" "non-top-namespaces" ""))))

(deftest ws-out-components-next-
  (is (= [":all" ":all-bricks" ":dev" ":latest-sha" ":loc" ":project"
          "brick" "get" "project" "since"]
         (candidates "ws" :next "out" "components" :next ""))))

(deftest ws-out-parentdir-
  (is (= true
         (contains? (set (candidates "ws" :next "out" ".." ""))
                    "polylith/"))))

(deftest ws-out-missing-
  (is (= []
         (candidates "ws" :next "out" "missing" ""))))

(deftest doc-example-profile
  (reset! engine/ws (-> "examples/doc-example/ws.edn" slurp read-string))
  (is (= ["+remote" ":project" ":resources" "brick" "color-mode" "project" "since"]
         (candidates "info" :next
                     "+default" :next
                     "loc" :next
                     "dev" :next
                     "all" :next
                     "all-bricks" :next))))
