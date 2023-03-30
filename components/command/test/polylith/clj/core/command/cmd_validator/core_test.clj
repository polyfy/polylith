(ns polylith.clj.core.command.cmd-validator.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.command.cmd-validator.core :as core]))

(deftest execute-test-command-from-the-workspace-root
  (is (= (core/validate {:settings {} :projects {}}
                        {:cmd "test"}
                        color/none)
         [true])))

(deftest execute-test-command-from-outside-the-workspace
  (is (= (core/validate nil
                        {:cmd "test"}
                        color/none)
         [false "  The command can only be executed from the workspace root."])))

(deftest execute-create-command-when-ws-file-is-set
  (is (= (core/validate {}
                        {:ws-file "ws.edn"
                         :cmd "create"
                         :args ["create" "c"]
                         :name "myname"}
                        color/none)
         [false "  The 'create' command can't be executed when the workspace is read from file via 'ws-file'."])))

(deftest execute-help-command-from-outside-the-workspace
  (is (= (core/validate nil
                        {:cmd "help"}
                        color/none)
         [true])))
