(ns polylith.clj.core.change.brick-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.file.interface :as file]
            [polylith.clj.core.change.entity :as brick]))

(def files ["bases/core/src/polylith/core/main.clj"
            "bases/core/test/polylith/core/main_test.clj"
            "bases/core/test/polylith/workspace/main_test.clj"
            "components/cmd/src/polylith/cmd/compile.clj"
            "components/cmd/src/polylith/cmd/test.clj"
            "components/workspace/src/polylith/core/core.clj"
            "components/workspace/src/polylith/core/interface.clj"
            "components/workspace/src/polylith/core/interfaces.clj"
            "deps.edn"
            "todo.txt"])

(deftest bricks--when-having-a-list-of-changed-files--return-bases-and-components
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:changed-bases ["core"]
            :changed-components ["cmd" "workspace"]
            :changed-environments []}
           (brick/changed-entities "." files)))))

(deftest changes--changes-made-between-two-commits--will-return-changed-components-and-bases
  (with-redefs [file/exists (fn [_] true)]
    (is (= {:changed-bases        ["migrator-cli"
                                   "poly-cli"]
            :changed-components   ["api"
                                   "change"
                                   "command"
                                   "common"
                                   "creator"
                                   "deps"
                                   "file"
                                   "git"
                                   "help"
                                   "lib-dep"
                                   "migrator"
                                   "path-finder"
                                   "shell"
                                   "test-helper"
                                   "test-runner"
                                   "text-table"
                                   "user-config"
                                   "user-input"
                                   "util"
                                   "validator"
                                   "workspace"
                                   "workspace-clj"
                                   "ws-explorer"]
            :changed-environments ["core"
                                   "development"
                                   "migrator"
                                   "poly"]}
           (brick/changes "."
                          "0aaeb588b004d48a6f34f35be52d204d568af9ec"
                          "9b3cc9e48947b016d546ae70259c3473e790e0e6")))))
