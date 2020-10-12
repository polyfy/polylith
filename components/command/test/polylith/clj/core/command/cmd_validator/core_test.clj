(ns polylith.clj.core.command.cmd-validator.core-test
  (:require [clojure.test :refer :all]
            [polylith.clj.core.util.interface.color :as color]
            [polylith.clj.core.command.cmd-validator.core :as core]))

(deftest execute-test-command-from-the-workspace-root
  (is (= [true]
         (core/validate {:settings {} :environments {}}
                        {:cmd "test"}
                        color/none))))

(deftest execute-test-command-from-outside-the-workspace
  (is (= [false "  The command can only be executed from the workspace root."]
         (core/validate nil
                        {:cmd "test"}
                        color/none))))

(deftest execute-create-command-when-ws-file-is-set
  (is (= [false "  The 'create' command can't be executed when the workspace is read from file via 'ws-file'."]
         (core/validate nil
                        {:ws-file "ws.edn"
                         :cmd "create"}
                        color/none))))

(deftest execute-help-command-from-outside-the-workspace
  (is (= [true]
         (core/validate nil
                        {:cmd "help"}
                        color/none))))
