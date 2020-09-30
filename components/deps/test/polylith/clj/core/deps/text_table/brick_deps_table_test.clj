(ns polylith.clj.core.deps.text-table.brick-deps-table-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.common.interface :as common]
            [polylith.clj.core.deps.text-table.brick-deps-table :as brick-deps-table]))

(def workspace {:interfaces [{:name "workspace-clj",
                              :type "interface",}
                             {:name "test-runner",
                              :type "interface",}
                             {:name "command",
                              :type "interface",}
                             {:name "text-table",
                              :type "interface",}
                             {:name "util",
                              :type "interface",}
                             {:name "validator",
                              :type "interface",}
                             {:name "user-input",
                              :type "interface",}
                             {:name "shell",
                              :type "interface",}
                             {:name "workspace",
                              :type "interface",}
                             {:name "user-config",
                              :type "interface",}
                             {:name "git",
                              :type "interface",}
                             {:name "deps",
                              :type "interface",}
                             {:name "help",
                              :type "interface",}
                             {:name "creator",
                              :type "interface",}
                             {:name "file",
                              :type "interface",}
                             {:name "entity",
                              :type "interface",}
                             {:name "test-helper",
                              :type "interface",}
                             {:name "common",
                              :type "interface",}
                             {:name "change",
                              :type "interface",}]
                :ws-dir ".",
                :name "polylith",
                :settings {:color-mode "none"},
                :environments [{:name "development",
                                :alias "dev",
                                :type "environment",
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
                                                  :indirect ["entity" "file" "git" "shell" "text-table" "validator"]},
                                       "text-table" {:direct ["util"], :indirect []},
                                       "util" {:direct [], :indirect []},
                                       "validator" {:direct ["common" "deps" "file" "util"], :indirect ["text-table"]},
                                       "user-input" {:direct [], :indirect []},
                                       "shell" {:direct [], :indirect []},
                                       "workspace" {:direct ["common" "deps" "entity" "file" "text-table" "util" "validator"],
                                                    :indirect []},
                                       "cli" {:direct ["change"
                                                       "command"
                                                       "common"
                                                       "file"
                                                       "user-input"
                                                       "util"
                                                       "workspace"
                                                       "workspace-clj"],
                                              :indirect ["creator"
                                                         "deps"
                                                         "entity"
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
                                       "entity" {:direct ["file" "util"], :indirect []},
                                       "test-helper" {:direct ["change"
                                                               "command"
                                                               "file"
                                                               "git"
                                                               "user-config"
                                                               "user-input"
                                                               "workspace"
                                                               "workspace-clj"],
                                                      :indirect ["common"
                                                                 "creator"
                                                                 "deps"
                                                                 "entity"
                                                                 "help"
                                                                 "shell"
                                                                 "test-runner"
                                                                 "text-table"
                                                                 "util"
                                                                 "validator"]},
                                       "common" {:direct ["util"], :indirect []},
                                       "change" {:direct ["common" "git" "util"], :indirect ["shell"]}},}]
                :components [{:name "change",
                              :type "component",
                              :interface-deps ["common" "git" "util"],}
                             {:name "command",
                              :type "component",
                              :interface-deps ["common" "creator" "deps" "help" "test-runner" "user-config" "util" "workspace"],
                              :lib-deps ["clojure"]}
                             {:name "common",
                              :type "component",
                              :interface-deps ["util"],}
                             {:name "creator",
                              :type "component",
                              :interface-deps ["common" "file" "git" "user-config" "util"],}
                             {:name "deps",
                              :type "component",
                              :interface-deps ["common" "text-table" "util"],}
                             {:name "entity",
                              :type "component",
                              :interface-deps ["file" "util"],}
                             {:name "file",
                              :type "component",
                              :interface-deps ["util"],}
                             {:name "git",
                              :type "component",
                              :interface-deps ["shell"],}
                             {:name "help",
                              :type "component",
                              :interface-deps ["util"],}
                             {:name "shell",
                              :type "component",
                              :interface-deps [],}
                             {:name "test-helper",
                              :type "component",
                              :interface-deps ["change" "command" "file" "git" "user-config" "user-input" "workspace" "workspace-clj"],}
                             {:name "test-runner",
                              :type "component",
                              :interface-deps ["common" "util"],}
                             {:name "text-table",
                              :type "component",
                              :interface-deps ["util"],}
                             {:name "user-config",
                              :type "component",
                              :interface-deps ["util"],}
                             {:name "user-input",
                              :type "component",
                              :interface-deps [],}
                             {:name "util",
                              :type "component",
                              :interface-deps [],}
                             {:name "validator",
                              :type "component",
                              :interface-deps ["common" "deps" "file" "util"],}
                             {:name "workspace",
                              :type "component",
                              :interface-deps ["common" "deps" "entity" "file" "text-table" "util" "validator"],}
                             {:name "workspace-clj",
                              :type "component",
                              :interface-deps ["common" "file" "user-config" "util"],}]
                :changes {:sha1 "HEAD",
                          :git-command "git diff HEAD --name-only",
                          :changed-components ["deps" "entity" "text-table" "workspace"],
                          :changed-bases [],
                          :changed-environments ["cli" "core"],
                          :env-to-indirect-changes {"cli" ["cli" "command" "test-helper" "validator"],
                                                    "core" ["cli" "command" "test-helper" "validator"],
                                                    "development" ["cli" "command" "test-helper" "validator"]},
                          :env-to-bricks-to-test {"cli" ["command" "deps" "entity" "validator" "workspace"], "core" [], "development" []},
                          :environments-to-test [],}
                :bases [{:name "cli",
                         :type "base",
                         :interface-deps ["change" "command" "common" "file" "user-input" "util" "workspace" "workspace-clj"],}]})

(def environments (:environments workspace))
(def components (:components workspace))
(def environment (common/find-environment "dev" environments))
(def component (common/find-component "workspace" components))

(deftest table--when-having-a-list-of-dependers-and-dependees--return-correct-table
  (is (= ["  used by      <  workspace  >  uses      "
          "  -----------                   ----------"
          "  command                       common    "
          "  test-helper                   deps      "
          "  cli                           entity    "
          "                                file      "
          "                                text-table"
          "                                util      "
          "                                validator "]
         (brick-deps-table/table workspace environment component))))
